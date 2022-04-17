package com.yundingxi.tell.service.Impl.upgrade;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.Reply;
import com.yundingxi.tell.bean.vo.IndexLetterVo;
import com.yundingxi.tell.bean.vo.LetterWebsocketVo;
import com.yundingxi.tell.bean.vo.submessage.SubMessageParam;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.mapper.ReplyMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import com.yundingxi.tell.util.strategy.SubMessageStrategyContext;
import com.yundingxi.tell.bean.dto.*;
import com.yundingxi.tell.bean.dto.*;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.common.websocket.WebSocketServer;
import com.yundingxi.tell.util.JsonUtil;
import com.yundingxi.tell.util.message.ScheduledUtil;
import com.yundingxi.tell.util.message.SendMailUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @version v1.0
 * @ClassName AbstractLetterServiceImpl
 * @Author rayss
 * @Datetime 2021/7/28 3:44 下午
 */
@Component
public abstract class AbstractLetterServiceImpl implements LetterService {
    public final LetterMapper letterMapper;

    public final RedisUtil redisUtil;

    private final ReplyMapper replyMapper;

    private final UserMapper userMapper;

    @Autowired
    public AbstractLetterServiceImpl(LetterMapper letterMapper, RedisUtil redisUtil, ReplyMapper replyMapper, UserMapper userMapper) {
        this.letterMapper = letterMapper;
        this.redisUtil = redisUtil;
        this.replyMapper = replyMapper;
        this.userMapper = userMapper;
    }

    @SneakyThrows
    @Override
    public Integer saveSingleLetter(LetterStorageDto letterStorageDto) {
        return CompletableFuture.supplyAsync(() -> {
            Letter letter = new Letter(UUID.randomUUID().toString()
                    , letterStorageDto.getStampUrl()
                    , letterStorageDto.getOpenId()
                    , letterStorageDto.getContent()
                    , letterStorageDto.getState()
                    , letterStorageDto.getPenName()
                    , letterStorageDto.getTapIds(), new Date());
            String tapIds = letter.getTapIds();
            String[] tabIdArr = tapIds.split(",");
            for (String tabId : tabIdArr) {
                letterMapper.updateLetterTap(tabId);
            }
            return letterMapper.insertSingleLetter(letter);
        }).get();
        //459399250
        //166799250
    }

    @SneakyThrows
    @Override
    @Deprecated
    public List<UnreadMessageDto> putUnreadMessage(String openId) {
        return CompletableFuture.supplyAsync(() -> {
            //使用redis获取
            @SuppressWarnings("unchecked") List<UnreadMessageDto> unreadMessage = (List<UnreadMessageDto>) redisUtil.get("letter:" + openId + ":unread_message");
            if (unreadMessage != null) {
                redisUtil.del("letter:" + openId + ":unread_message");
            }
            return unreadMessage;
        }).get();
    }


    /**
     * 获取信件的模版方法，不允许重写，对于获取具体三封信件的逻辑可以进行对customGetLettersByOpenId的方法重写
     * @param openId 用户 open id
     * @return 顺序获取三封信件
     */
    @SneakyThrows
    @Override
    public final List<IndexLetterDto> getLettersUpgrade(String openId){
        return CompletableFuture.supplyAsync(() -> {
            /*
             * 1.从缓存获取此用户已经获取到的信件信息，包括获取时间、上次访问数据库时候的信件数量(最开始时候默认为0)
             *  - 读取数据库信件数量，如果大于上次访问的信件数量说明数据已经更新，那么读取最新的最后十条数据然后获取返回，不大于的话直接返回为null，
             *    随机生成3个数字nextInt(10)，用来从最新的十篇中抽取三篇
             * */
            String listKey = "IndexLetterDtoList";
            String letterInfoKey = "letter:" + openId + ":letter_info";
            Object o = redisUtil.get(letterInfoKey);
            if (o == null) {
                setLetterInitInfoByOpenId(openId);
            }
            String letterInfoJson = (String) redisUtil.get(letterInfoKey);
            JsonNode letterInfoJsonNode = JsonUtil.parseJson(letterInfoJson);
            String lastDate = letterInfoJsonNode.findPath("date").toString();
            String currentDate = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            //-----------------这里不该是总数，为了体现出没有被回复的信件优先被推荐、时间较新原则，应该是一个数量区间
            //待解决
            int totalNumber = letterMapper.selectTotalNumberNonSelf(openId);
            int visibleNumber = letterInfoJsonNode.findPath("visitNumber").asInt();
            if (lastDate.equals(currentDate) || visibleNumber >= totalNumber) {
                //此时说明当天已经访问过，所以不用再查询而是直接从缓存中获取,数据库数量大于缓存中的数量表示数据库已经更新，则重新获取，否则也不再重新获取，而是直接获取缓存中的信件数据
                @SuppressWarnings("unchecked") List<IndexLetterDto> indexLetterDtoList = (List<IndexLetterDto>) JSONObject.parse(letterInfoJsonNode.findPath(listKey).toString());
                return indexLetterDtoList;
            } else {
                List<IndexLetterDto> indexLetterDtoList = customGetLettersByOpenId(openId,totalNumber);

                updateRedisLetterInfo(letterInfoKey, totalNumber, currentDate, indexLetterDtoList);
                return indexLetterDtoList;
            }
        }).get();
    }

    /**
     * 此方法是自定义获取三封信件的方法，交给子类完成
     * @param openId 用户 open id
     * @param totalNumber 数据库信件的总数
     * @return 三封信件
     */
    protected abstract List<IndexLetterDto> customGetLettersByOpenId(String openId,int totalNumber);

    /**
     * 更新redis中letter info的缓存内容
     *
     * @param key         此缓存中对应redis的key名
     * @param visitNumber 此时访问数据库时候数据库中的数据量
     * @param currentDate 当前日期字符串
     * @param list        存储着信件的列表
     */
    private void updateRedisLetterInfo(String key, int visitNumber, String currentDate, List<IndexLetterDto> list) {
        //更新redis缓存内容
        JSONObject object = new JSONObject();
        object.put("visitNumber", visitNumber);
        object.put("date", currentDate);
        object.put("IndexLetterDtoList", list);
        redisUtil.set(key, object.toJSONString());
    }

    /**
     * 获取length个不同数字的数组
     *
     * @param surplusThreshold 求余的阈值
     * @param length           要获取的长度
     * @return 不同数字数组
     */
    private int[] getDifferentArray(int surplusThreshold, int length) {
        Random random = new Random();
        int[] differentArray = new int[length];
        for (int i = 0; i < differentArray.length; i++) {
            differentArray[i] = spinRandomNumberToNonExist(random.nextInt(surplusThreshold), surplusThreshold, i, differentArray);
        }
        return differentArray;
    }

    /**
     * 自旋直到产生没有出现过的数字
     *
     * @param randomNumber  随机数字
     * @param spinThreshold 自旋阈值，表示每次对哪个数字进行取余
     * @param length        已经有的数字长度
     * @param alreadyNumber 已经出现的数字
     * @return 自旋数字结果
     */
    private int spinRandomNumberToNonExist(int randomNumber, int spinThreshold, int length, int... alreadyNumber) {
        for (; ; ) {
            //成功标记，表示着是否已经不存在重复数字
            boolean successFlag = true;
            for (int i = 0; i < length; i++) {
                if (alreadyNumber[i] == randomNumber) {
                    randomNumber = (randomNumber + 1) % spinThreshold;
                    successFlag = false;
                }
            }
            if (successFlag) {
                return randomNumber;
            }
        }
    }

    /**
     * @param openId 用户 open id
     * @return 顺序获取三封信件
     */
    @Deprecated
    @SneakyThrows
    @Override
    public List<IndexLetterDto> getLettersByOpenId(String openId) {
        return CompletableFuture.supplyAsync(() -> {
            Object o = redisUtil.get("letter:" + openId + ":letter_info");
            if (o == null) {
                setLetterInitInfoByOpenId(openId);
            }
            Object obj = redisUtil.get("letter:" + openId + ":letter_info");
            String currentDate = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int letterCountLocation = 0;
            if (obj != null) {
                JsonNode letterInfo = JsonUtil.parseJson((String) obj);
                String date = letterInfo.findPath("date").toString().replace("\"", "");
                letterCountLocation = Integer.parseInt(letterInfo.findPath("letter_count_location").toString().trim().replace("\"", ""));
                if (!currentDate.equals(date)) {
                    letterCountLocation = letterCountLocation + 3;
                }
            }
            if (letterCountLocation != 0 || obj == null) {
                ObjectNode newValue = JsonNodeFactory.instance.objectNode().putObject("letter_info");
                newValue.put("date", currentDate);
                newValue.put("letter_count_location", letterCountLocation);
                redisUtil.set("letter:" + openId + ":letter_info", newValue.toPrettyString(), TimeUnit.HOURS.toSeconds(12));
            }
            List<Letter> letters = letterMapper.selectLetterLimit(letterCountLocation, openId, 1);
            return GeneralDataProcessUtil.configLetterDataFromList(letters, openId);
        }).get();
    }

    @Override
    public void saveReplyFromSenderToRecipient(Reply reply) {
        CompletableFuture.runAsync(() -> letterMapper.insertReply(reply));
    }

    @SneakyThrows
    @Override
    public String replyLetter(LetterReplyDto letterReplyDto) {
        CompletableFuture.runAsync(() -> {
            String replyId = UUID.randomUUID().toString();
            //回信订阅消息
            SubMessageParam param = new SubMessageParam(letterReplyDto.getLetterId(), letterReplyDto.getMessage(), "", letterReplyDto.getSenderPenName(), letterReplyDto.getRecipient(), letterReplyDto.getSender(), letterReplyDto, WeChatEnum.SUB_MESSAGE_REPLY_LETTER_TEMPLATE_ID, WeChatEnum.SUB_MESSAGE_REPLY_PAGE, WeChatEnum.SUB_MESSAGE_MINI_PROGRAM_STATE_FORMAL_VERSION);
            SubMessageStrategyContext.getSubMessageStrategy(WeChatEnum.SUB_MESSAGE_REPLY_LETTER_TEMPLATE_ID).processSubMessage(param, replyId);

            Reply reply = new Reply(replyId, letterReplyDto.getLetterId(), new Date(), letterReplyDto.getMessage(), letterReplyDto.getSender(), letterReplyDto.getSenderPenName());
            String replyTime = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            letterMapper.insertReply(reply);
            @SuppressWarnings("unchecked") LinkedList<UnreadMessageDto> messageDtoList = (LinkedList<UnreadMessageDto>) redisUtil.get("letter:" + letterReplyDto.getRecipient() + ":unread_message");
            UnreadMessageDto messageDto = new UnreadMessageDto(letterReplyDto.getSender()
                    , letterReplyDto.getRecipient()
                    , letterReplyDto.getMessage()
                    , replyTime, letterReplyDto.getSenderPenName()
                    , letterMapper.selectPenNameById(letterReplyDto.getLetterId())
                    , letterReplyDto.getLetterId(), replyId);
            String reserveString = "letter:" + letterReplyDto.getRecipient() + "_reserve:reply";
            @SuppressWarnings("unchecked") LinkedList<UnreadMessageDto> reserveReply = (LinkedList<UnreadMessageDto>) redisUtil.get(reserveString);
            LinkedList<UnreadMessageDto> list = new LinkedList<>();
            list.add(messageDto);
            if (messageDtoList == null) {
                redisUtil.set("letter:" + letterReplyDto.getRecipient() + ":unread_message", list);
            } else {
                messageDtoList.addFirst(messageDto);
                redisUtil.set("letter:" + letterReplyDto.getRecipient() + ":unread_message", messageDtoList);
            }
            if (reserveReply == null) {
                redisUtil.set(reserveString, list);
            } else {
                reserveReply.addFirst(messageDto);
                redisUtil.set(reserveString, reserveReply);
            }
        }).get();
        return "success";
    }

    @SneakyThrows
    @Override
    public Map<Integer, Integer> getNumberOfLetter(String openId) {
        return CompletableFuture.supplyAsync(() -> {
            @SuppressWarnings("unchecked") List<UnreadMessageDto> messageDtoList = (List<UnreadMessageDto>) redisUtil.get("letter:" + openId + ":unread_message");
            Map<Integer, Integer> map = new HashMap<>(10);
            map.put(1, messageDtoList == null ? 0 : messageDtoList.size());
            Object commCount = redisUtil.get("comm:" + openId + ":count");
            map.put(2, commCount == null ? 0 : (Integer) commCount);
            String achieveUnreadNumKey = "listener:" + openId + ":achieve_unread_num";
            String stampUnreadNumKey = "listener:" + openId + ":stamp_unread_num";
            Integer achieveNum = (Integer) redisUtil.get(achieveUnreadNumKey);
            Integer stampNum = (Integer) redisUtil.get(stampUnreadNumKey);
            map.put(3, achieveNum == null ? 0 : achieveNum);
            map.put(4, stampNum == null ? 0 : stampNum);
            return map;
        }).get();
    }

    @SneakyThrows
    @Override
    public List<UnreadMessageDto> getAllUnreadLetter(String openId, Integer pageNum) {
        return CompletableFuture.supplyAsync(() -> {
            @SuppressWarnings("unchecked") List<UnreadMessageDto> messageDtoList = (List<UnreadMessageDto>) redisUtil.get("letter:" + openId + ":unread_message");
            //  delete the key
            if (messageDtoList != null) {
                redisUtil.del("letter:" + openId + ":unread_message");
            }
            return messageDtoList != null ? messageDtoList : new ArrayList<UnreadMessageDto>();
        }).get();
    }

    @Override
    @Deprecated
    public LetterDto getLetterById(String letterId) {
        Letter letter = letterMapper.selectLetterById(letterId);
        return BeanUtil.toBean(letter, LetterDto.class);
    }

    @SneakyThrows
    @Override
    public void setLetterInitInfoByOpenId(String openId) {
        CompletableFuture.runAsync(() -> {
            String letterInfoKey = "letter:" + openId + ":letter_info";
            Object o = redisUtil.get(letterInfoKey);
            if (o != null) {
                return;
            }
            ObjectNode letterInfo = JsonNodeFactory.instance.objectNode().putObject(openId + "_letter_info");
            letterInfo.put("date", "");
            letterInfo.put("visitNumber", 0);
            letterInfo.put("IndexLetterDtoList", "null");
            redisUtil.set(letterInfoKey, letterInfo.toPrettyString());
        }).get();
    }

    @SneakyThrows
    @Override
    public LetterDto getLetterById(ReplyInfoDto replyInfoDto) {
        return CompletableFuture.supplyAsync(() -> {
            String recipientPenName = userMapper.selectPenNameByOpenId(replyInfoDto.getOpenId());
            if (replyInfoDto.getLetterId() == null || "".equals(replyInfoDto.getLetterId().replace("\"", ""))) {
                Letter letter = letterMapper.selectLetterById(replyInfoDto.getReplyId());
                return new LetterDto(null, letter.getContent(), replyInfoDto.getReplyId(), letter.getPenName(), recipientPenName, letter.getReleaseTime());
            } else {
                Reply reply = replyMapper.selectReplyById(replyInfoDto.getReplyId());
                return new LetterDto(
                        letterMapper.selectContentByLetterId(reply.getLetterId())
                        , reply.getContent(), reply.getId(), reply.getPenName()
                        , recipientPenName, reply.getReplyTime()
                );
            }
        }).get();

    }

    @SneakyThrows
    @Override
    public IndexLetterDto getLetterById(IndexLetterVo indexLetterVo) {
        return CompletableFuture.supplyAsync(() -> {
            String recipientPenName = userMapper.selectPenNameByOpenId(indexLetterVo.getOpenId());
            Letter letter = letterMapper.selectLetterById(indexLetterVo.getLetterId());
            return new IndexLetterDto(letter.getContent(), letter.getOpenId(), letter.getId(), letter.getPenName(), null, recipientPenName, letter.getReleaseTime());
        }).get();
    }

    @Override
    public Result<PageInfo<UnreadMessageDto>> getLetterOfHistory(String openId, Integer pageNum) {
        String reserveString = "letter:" + openId + "_reserve:reply";
        @SuppressWarnings("unchecked") List<UnreadMessageDto> reserveReply = (List<UnreadMessageDto>) redisUtil.get(reserveString);
        PageHelper.startPage(pageNum, 10);
        return ResultGenerator.genSuccessResult(new PageInfo<>(reserveReply == null ? new ArrayList<>() : reserveReply));
    }

    @Override
    public int changeLetterState(String id, int state) {
        return letterMapper.updateLetterState(id, state);
    }

    public String replyLetterByWebSocket(LetterReplyDto letterReplyDto) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextInt = random.nextInt(2, 7);
        String recipientPenName = userMapper.selectPenNameByOpenId(letterReplyDto.getRecipient());
        String arrivalTime = JsonNodeFactory.instance.objectNode().put("arrivalTime", nextInt).toPrettyString();
        ScheduledUtil.delayNewTask(() -> SendMailUtil.enMessageToQueue(
                new LetterWebsocketVo(letterReplyDto.getSender()
                        , letterReplyDto.getRecipient()
                        , letterReplyDto.getLetterId()
                        , letterReplyDto.getSenderPenName()
                        , recipientPenName
                        , letterReplyDto.getMessage().length() > 25 ? letterReplyDto.getMessage().substring(0, 25) + "..." : letterReplyDto.getMessage() + "..."
                        , WebSocketServer.getServerByOpenId(letterReplyDto.getRecipient())
                )), 0);
        return arrivalTime;
    }
}
