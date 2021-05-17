package com.yundingxi.tell.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.dto.*;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.Reply;
import com.yundingxi.tell.bean.vo.IndexLetterVo;
import com.yundingxi.tell.bean.vo.LetterWebsocketVo;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.common.websocket.WebSocketServer;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.mapper.ReplyMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import com.yundingxi.tell.util.JsonUtil;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import com.yundingxi.tell.util.message.ScheduledUtil;
import com.yundingxi.tell.util.message.SendMailUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * @ClassName LetterServiceImpl
 * @Author rayss
 * @Datetime 2021/3/24 6:31 下午
 */

@Service
public class LetterServiceImpl implements LetterService {

    private final Logger log = LoggerFactory.getLogger(LetterServiceImpl.class);

    private final LetterMapper letterMapper;

    private final RedisUtil redisUtil;

    private final ReplyMapper replyMapper;

    private final UserMapper userMapper;

    @Autowired
    public LetterServiceImpl(LetterMapper letterMapper, RedisUtil redisUtil, ReplyMapper replyMapper, UserMapper userMapper) {
        this.letterMapper = letterMapper;
        this.redisUtil = redisUtil;
        this.replyMapper = replyMapper;
        this.userMapper = userMapper;
    }

    @SneakyThrows
    @Override
    public String saveSingleLetter(LetterStorageDto letterStorageDto) {
        Integer result = CompletableFuture.supplyAsync(() -> {
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
        return result == 1 ? JsonNodeFactory.instance.objectNode().put("arrivalTime", 0).toPrettyString() : "保存信件失败";
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
     * 这里需要做出优化
     * 1.优先根据标签获取信件，如何判断
     * - "letter:" + openId + ":letter_info"这个缓存中存储每个用户的喜爱(此用户回信的信标签)的对应标签数量
     * 获取两则占比最大的，然后这个表示最有可能推荐的，查询数据库时候按照标签进行分组，获取数据库中这个标签组中两片信件
     * - 如果上述条件获取的是两封，然后这个是获取其他不相干类型的一封，以来扩展
     * - 都要保证时间优先，也就是优先把最新的信件推送出去
     * - 随机获取，不可以排着数据库获取，而是随机获取，当然每日不可以重复
     * <p>
     * 1.从缓存中获取已经读取到的位置数据，用来判断数据库中的数据是否是最新的
     *
     * @param openId 用户 open id
     * @return 获取三封信件
     */
    @SneakyThrows
    @Override
    public List<IndexLetterDto> getRandomLettersAndLatestByOpenId(String openId) {
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
            int totalNumber = letterMapper.selectTotalNumber(openId);
            int visibleNumber = letterInfoJsonNode.findPath("visitNumber").asInt();
            if (lastDate.equals(currentDate) || visibleNumber >= totalNumber) {
                //此时说明当天已经访问过，所以不用再查询而是直接从缓存中获取,数据库数量大于缓存中的数量表示数据库已经更新，则重新获取，否则也不再重新获取，而是直接获取缓存中的信件数据
                @SuppressWarnings("unchecked") List<IndexLetterDto> indexLetterDtoList = (List<IndexLetterDto>) JSONObject.parse(letterInfoJsonNode.findPath(listKey).toString());
                return indexLetterDtoList;
            } else {
                //此时需要从数据库获取内容,随机三个数字获取数据库中最新的十条数据中的位置
                Random random = new Random();
                int gainLetterNumber = 3;
                List<IndexLetterDto> indexLetterDtoList = new ArrayList<>(3);
                //用来解决生成随机数重复问题，以防出现相同的信件，当然如果数据库的数据比较少，进行randomInt+1之后还是会有重复
                int num1 = 0, num2 = 0;
                for (int i = 0; i < gainLetterNumber; i++) {
                    int randomInt = random.nextInt(10);
                    if (i == 1 && randomInt == num1) {
                        randomInt = randomInt + 1;
                        num2 = randomInt;
                    } else if (i == 2 && randomInt == num1 || randomInt == num2) {
                        //此时就是如果是第三次获取，出现的随机数是之前出现过的某一种都要进行+1操作
                        randomInt = randomInt + 1;
                        if(randomInt == num1 || randomInt == num2){
                            //这个是做一个双重判断，以防+1后等于下个值
                            randomInt = randomInt + 1;
                        }
                    } else {
                        num1 = randomInt;
                    }
                    Letter letter = letterMapper.selectRandomLatestLetter(openId, randomInt % totalNumber, 1, 1);
                    GeneralDataProcessUtil.configLetterDataFromSingleObject(letter, openId, indexLetterDtoList);
                }
                //更新redis缓存内容
                JSONObject object = new JSONObject();
                object.put("visitNumber", totalNumber);
                object.put("date", currentDate);
                object.put(listKey, indexLetterDtoList);
                redisUtil.set(letterInfoKey, object.toJSONString());
                return indexLetterDtoList;
            }
        }).get();
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

        return "";
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
