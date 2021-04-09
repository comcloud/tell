package com.yundingxi.tell.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.bean.dto.LetterDto;
import com.yundingxi.tell.bean.dto.LetterReplyDto;
import com.yundingxi.tell.bean.dto.UnreadMessageDto;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.Reply;
import com.yundingxi.tell.bean.vo.LetterVo;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.common.websocket.WebSocketServer;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.JsonUtil;
import com.yundingxi.tell.util.message.ScheduledUtil;
import com.yundingxi.tell.util.message.SendMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @version v1.0
 * @ClassName LetterServiceImpl
 * @Author rayss
 * @Datetime 2021/3/24 6:31 下午
 */

@Slf4j
@Service
public class LetterServiceImpl implements LetterService {

    private final LetterMapper letterMapper;

    private final RedisUtil redisUtil;

    @Autowired
    public LetterServiceImpl(LetterMapper letterMapper, RedisUtil redisUtil) {
        this.letterMapper = letterMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public String saveSingleLetter(Letter letter) {
        letter.setId(UUID.randomUUID().toString());
        letter.setState(1);
        String tapIds = letter.getTapIds();
        String[] tabIdArr = tapIds.split(",");
        for (String tabId : tabIdArr) {
            letterMapper.updateLetterTap(tabId);
        }
        letterMapper.insertSingleLetter(letter);
        return JsonNodeFactory.instance.objectNode().put("arrivalTime", 0).toPrettyString();
    }

    @Override
    public List<UnreadMessageDto> putUnreadMessage(String openId) {
        //使用redis获取
        @SuppressWarnings("unchecked") List<UnreadMessageDto> unreadMessage = (List<UnreadMessageDto>) redisUtil.get(openId + "_unread_message");
        if (unreadMessage != null) {
            redisUtil.del(openId + "_unread_message");
        }
        return unreadMessage;
    }

    @Override
    public List<LetterDto> getLettersByOpenId(String openId) {
        Object o = redisUtil.get(openId + "_letter_info");
        if(o == null){
            setLetterInitInfoByOpenId(openId);
        }
        JsonNode letterInfo = JsonUtil.parseJson((String) redisUtil.get(openId + "_letter_info"));
        String date = letterInfo.findPath("date").toString().replace("\"", "");
        int letterCountLocation = Integer.parseInt(letterInfo.findPath("letter_count_location").toString().trim().replace("\"", ""));
        String currentDate = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!currentDate.equals(date)) {
            letterCountLocation = letterCountLocation + 3;
            ObjectNode newValue = JsonNodeFactory.instance.objectNode().putObject("letter_info");
            newValue.put("date", currentDate);
            newValue.put("letter_count_location", letterCountLocation);
            redisUtil.set(openId + "_letter_info", newValue.toPrettyString(), TimeUnit.HOURS.toSeconds(12));
        }
        List<Letter> letters = letterMapper.selectLetterLimit(letterCountLocation);
        List<LetterDto> letterDtoList = new ArrayList<>();
        letters.forEach(letter -> {
            LetterDto letterDto = BeanUtil.toBean(letter, LetterDto.class);
            letterDtoList.add(letterDto);
        });
        return letterDtoList;
    }

    @Override
    public void saveReplyFromSenderToRecipient(Reply reply) {
        letterMapper.insertReply(reply);
    }

    @Override
    public String replyLetter(LetterReplyDto letterReplyDto) {
        Reply reply = BeanUtil.toBean(letterReplyDto, Reply.class);
        String replyTime = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        reply.setId(UUID.randomUUID().toString());
        reply.setContent(letterReplyDto.getMessage());
        reply.setReplyTime(new Date());
        reply.setOpenId(letterReplyDto.getSender());
        letterMapper.insertReply(reply);
        @SuppressWarnings("unchecked") List<UnreadMessageDto> messageDtoList = (List<UnreadMessageDto>) redisUtil.get(letterReplyDto.getRecipient() + "_unread_message");
        UnreadMessageDto messageDto = BeanUtil.toBean(letterReplyDto, UnreadMessageDto.class);
        messageDto.setSenderTime(replyTime);
        if(messageDtoList == null){
            List<UnreadMessageDto> list = new ArrayList<>();
            list.add(messageDto);
            redisUtil.set(letterReplyDto.getRecipient()+"_unread_message",list);
        }else{
            messageDtoList.add(messageDto);
            redisUtil.set(letterReplyDto.getRecipient()+"_unread_message",messageDtoList);
        }
        return "";
    }

    @Override
    public Map<Integer, Integer> getNumberOfLetter(String openId) {
        @SuppressWarnings("unchecked") List<UnreadMessageDto> messageDtoList = (List<UnreadMessageDto>) redisUtil.get(openId + "_unread_message");
        Map<Integer, Integer> map = new HashMap<>(10);
        map.put(1, messageDtoList == null ? 0 : messageDtoList.size());
        return map;
    }

    @Override
    public List<UnreadMessageDto> getAllUnreadLetter(String openId) {
        @SuppressWarnings("unchecked") List<UnreadMessageDto> messageDtoList = (List<UnreadMessageDto>) redisUtil.get(openId + "_unread_message");

        //  delete the key
//        if (messageDtoList != null) {
//            redisUtil.del(openId + "_unread_message");
//        }
        return messageDtoList;
    }

    @Override
    public LetterDto getLetterById(String letterId) {
        Letter letter = letterMapper.selectLetterById(letterId);
        return BeanUtil.toBean(letter, LetterDto.class);
    }

    @Override
    public void setLetterInitInfoByOpenId(String openId){
        ObjectNode letterInfo = JsonNodeFactory.instance.objectNode().putObject(openId + "_letter_info");
        String date = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        letterInfo.put("date", date);
        letterInfo.put("letter_count_location", 1);
        redisUtil.set(openId + "_letter_info", letterInfo.toPrettyString());

    }

    public String replyLetterByWebSocket(LetterReplyDto letterReplyDto) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextInt = random.nextInt(2, 7);
        String arrivalTime = JsonNodeFactory.instance.objectNode().put("arrivalTime", nextInt).toPrettyString();
        ScheduledUtil.delayNewTask(() -> SendMailUtil.enMessageToQueue(
                new LetterVo(letterReplyDto.getSender()
                        , letterReplyDto.getRecipient()
                        , letterReplyDto.getLetterId()
                        , letterReplyDto.getPenName()
                        , letterReplyDto.getMessage().length() > 25 ? letterReplyDto.getMessage().substring(0, 25) + "..." : letterReplyDto.getMessage() + "..."
                        , WebSocketServer.getServerByOpenId(letterReplyDto.getRecipient())
                )), 0);
        return arrivalTime;
    }
}
