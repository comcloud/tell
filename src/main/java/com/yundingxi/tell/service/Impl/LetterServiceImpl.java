package com.yundingxi.tell.service.Impl;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import com.yundingxi.tell.util.message.SendMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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
        letterMapper.insertSingleLetter(letter);
        return JsonNodeFactory.instance.objectNode().put("arrivalTime", 0).toPrettyString();
    }

    @Override
    public UnreadMessageDto putUnreadMessage(String openId) {
        //使用redis获取
        UnreadMessageDto unreadMessage = (UnreadMessageDto) redisUtil.get(openId + "_unread_message");
        if (unreadMessage != null) {
            redisUtil.del(openId + "_unread_message");
        }
        return unreadMessage;
    }

    @Override
    public List<Letter> getLettersByOpenId(String openId) {
        JsonNode letterInfo = JsonUtil.parseJson((String) redisUtil.get(openId + "_letter_info"));
        String date = letterInfo.findPath("date").toString();
        int letterCountLocation = Integer.parseInt(letterInfo.findPath("letter_count_location").toString().trim().replace("\"", ""));
        String currentDate = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!currentDate.equals(date)) {
            letterCountLocation = letterCountLocation + 3;
            ObjectNode newValue = JsonNodeFactory.instance.objectNode().putObject("letter_info");
            newValue.put("date", currentDate);
            newValue.put("letter_count_location", letterCountLocation);
            redisUtil.set(openId + "_letter_info", newValue.toPrettyString());
        }
        return letterMapper.selectLetterLimit(letterCountLocation);
    }

    @Override
    public void saveReplyFromSenderToRecipient(Reply reply) {
        letterMapper.insertReply(reply);
    }

    @Override
    public String replyLetter(LetterReplyDto letterReplyDto) {
        String arrivalTime = JsonNodeFactory.instance.objectNode().put("arrivalTime", 0).toPrettyString();
        SendMailUtil.enMessageToQueue(new LetterVo(letterReplyDto.getSender()
                ,letterReplyDto.getRecipient()
                ,letterReplyDto.getLetterId()
                ,letterReplyDto.getPenName()
                ,letterReplyDto.getMessage()
                , WebSocketServer.getServerByOpenId(letterReplyDto.getRecipient())
        ));
        return arrivalTime;
    }
}
