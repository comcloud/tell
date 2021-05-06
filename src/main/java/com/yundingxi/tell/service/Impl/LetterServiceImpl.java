package com.yundingxi.tell.service.Impl;

import cn.hutool.core.bean.BeanUtil;
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
            List<Letter> letters = letterMapper.selectLetterLimit(letterCountLocation, openId);
            return GeneralDataProcessUtil.configLetterDataFromList(letters,openId);
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
            @SuppressWarnings("unchecked") List<UnreadMessageDto> messageDtoList = (List<UnreadMessageDto>) redisUtil.get("letter:" + letterReplyDto.getRecipient() + ":unread_message");
            UnreadMessageDto messageDto = new UnreadMessageDto(letterReplyDto.getSender()
                    , letterReplyDto.getRecipient()
                    , letterReplyDto.getMessage()
                    , replyTime, letterReplyDto.getSenderPenName()
                    , letterMapper.selectPenNameById(letterReplyDto.getLetterId())
                    , letterReplyDto.getLetterId(), replyId);
            String reserveString = "letter:" + letterReplyDto.getRecipient() + "_reserve:reply";
            @SuppressWarnings("unchecked") List<UnreadMessageDto> reserveReply = (List<UnreadMessageDto>) redisUtil.get(reserveString);
            List<UnreadMessageDto> list = new ArrayList<>();
            list.add(messageDto);
            if (messageDtoList == null) {
                redisUtil.set("letter:" + letterReplyDto.getRecipient() + ":unread_message", list);
            } else {
                messageDtoList.add(messageDto);
                redisUtil.set("letter:" + letterReplyDto.getRecipient() + ":unread_message", messageDtoList);
            }
            if (reserveReply == null) {
                redisUtil.set(reserveString, list);
            } else {
                reserveReply.add(messageDto);
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

    @Override
    public void setLetterInitInfoByOpenId(String openId) {
        CompletableFuture.runAsync(() -> {
            Object o = redisUtil.get("letter:" + openId + ":letter_info");
            if (o != null) {
                return;
            }
            ObjectNode letterInfo = JsonNodeFactory.instance.objectNode().putObject(openId + "_letter_info");
            String date = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            letterInfo.put("date", date);
            letterInfo.put("letter_count_location", 0);
            redisUtil.set("letter:" + openId + ":letter_info", letterInfo.toPrettyString());
        });
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
        String orderBy = "senderTime desc";
        PageHelper.startPage(pageNum, 10, orderBy);
        return ResultGenerator.genSuccessResult(new PageInfo<>(reserveReply == null ? new ArrayList<>() : reserveReply));
    }

    @Override
    public int changeLetterState(String id, int state) {
        return letterMapper.updateLetterState(id,state);
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
