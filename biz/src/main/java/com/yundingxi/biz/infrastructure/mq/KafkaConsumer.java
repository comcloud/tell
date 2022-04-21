package com.yundingxi.biz.infrastructure.mq;

import com.alibaba.fastjson.JSONObject;
import com.yundingxi.biz.model.KafkaMessage;
import com.yundingxi.biz.service.TaskService;
import com.yundingxi.common.util.redis.RedisUtil;
import com.yundingxi.dao.mapper.AchieveMapper;
import com.yundingxi.dao.mapper.StampMapper;
import com.yundingxi.dao.mapper.TaskMapper;
import com.yundingxi.dao.model.Achieve;
import com.yundingxi.dao.model.SpittingGrooves;
import com.yundingxi.dao.model.UserAchieve;
import com.yundingxi.dao.model.UserStamp;
import com.yundingxi.model.dto.Diary.DiaryDto;
import com.yundingxi.model.dto.letter.LetterReplyDto;
import com.yundingxi.model.dto.letter.LetterStorageDto;
import com.yundingxi.model.vo.TimelineVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.yundingxi.common.model.constant.CommonConstant.*;

/**
 * 消息接收
 *
 * @author rayss
 */
@Component
@Slf4j
public class KafkaConsumer {


    private final RedisUtil redisUtil;

    private final AchieveMapper achieveMapper;

    private final StampMapper stampMapper;

    private final TaskMapper taskMapper;

    private final JdbcTemplate jdbcTemplate;

    private final TaskService taskService;

    @Autowired
    public KafkaConsumer(RedisUtil redisUtil, AchieveMapper achieveMapper, StampMapper stampMapper, TaskMapper taskMapper, JdbcTemplate jdbcTemplate, TaskService taskService) {
        this.redisUtil = redisUtil;
        this.achieveMapper = achieveMapper;
        this.stampMapper = stampMapper;
        this.taskMapper = taskMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.taskService = taskService;
    }


    /**
     * containerFactory:定义批处理器，批处理消费的线程数由kafka.listener.concurrencys控制
     * topics：消费的消息队列的topic
     */
    @KafkaListener(containerFactory = "kafkaBatchListener", clientIdPrefix = LETTER, topics = {ACHIEVE_STAMP_TOPIC})
    public void letterAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends KafkaMessage<? extends LetterStorageDto>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());
                KafkaMessage<? extends LetterStorageDto> kafkaMessage = record.value();
                LetterStorageDto letterStorageDto = kafkaMessage.getObject();
                String openId = letterStorageDto.getOpenId();
                String content = letterStorageDto.getContent();
                consumeMessage(openId, LETTER, content);
            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }
    }


    @KafkaListener( containerFactory = "kafkaBatchListener", clientIdPrefix = DIARY, topics = {ACHIEVE_STAMP_TOPIC})
    public void diaryAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends KafkaMessage<? extends DiaryDto>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());
                KafkaMessage<? extends DiaryDto> kafkaMessage = record.value();
                DiaryDto diaryDto = kafkaMessage.getObject();
                consumeMessage(diaryDto.getOpenId(), DIARY, diaryDto.getContent());
            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }

    }

    @KafkaListener(containerFactory = "kafkaBatchListener", clientIdPrefix = SPIT, topics = {ACHIEVE_STAMP_TOPIC})
    public void spitAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends KafkaMessage<? extends SpittingGrooves>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());
                KafkaMessage<? extends SpittingGrooves> kafkaMessage = record.value();
                SpittingGrooves spittingGrooves = kafkaMessage.getObject();
                consumeMessage(spittingGrooves.getOpenId(), SPIT, spittingGrooves.getContent());
            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }

    }

    @KafkaListener( containerFactory = "kafkaBatchListener", clientIdPrefix = REPLY, topics = {ACHIEVE_STAMP_TOPIC})
    public void replyAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends KafkaMessage<? extends LetterReplyDto>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());
                KafkaMessage<? extends LetterReplyDto> kafkaMessage = record.value();
                LetterReplyDto replyDto = kafkaMessage.getObject();
                consumeMessage(replyDto.getSender(), REPLY, replyDto.getMessage());
            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }

    }


    //-----暂时写到这里，需要重新思考一下下面的写法-------

    private void consumeMessage(String openId, String eventType, String content) {
        updateRedisTimeline(openId, eventType, content);
        JSONObject jsonObject = (JSONObject) redisUtil.get("listener:" + openId + ":offset");
        @SuppressWarnings("unchecked") ArrayList<String> list = (ArrayList<String>) jsonObject.get(eventType);
        if (list == null) {
            taskService.stampAndAchieveInitForEveryone(openId, true);
            jsonObject = (JSONObject) redisUtil.get("listener:" + openId + ":offset");
            list = (ArrayList<String>) jsonObject.get(eventType);
        }
        //此时通过获取到的位置以及属于的类型查询成就表然后判断对应的任务是否满足
        List<Achieve> achieveList = achieveMapper.selectAllTaskIdAndIdByAchieveTypeAndNonId(list, eventType);
        achieveList.forEach(achieve -> judgeAchieve(openId, eventType, (JSONObject) redisUtil.get("listener:" + openId + ":offset"), achieve));

    }

    /**
     * 判断成就是否满足
     *
     * @param openId      此用户open id
     * @param achieveType 成就类型
     * @param jsonObject  redis中存储用户的成就偏移量json对象
     * @param achieve     存储成就id与任务id的成就对象
     */
    private void judgeAchieve(String openId, String achieveType, JSONObject jsonObject, Achieve achieve) {
        String sqlStr = combineSqlString(openId, achieve);
        if ("".equals(sqlStr)) {
            return;
        }
        Integer result = jdbcTemplate.queryForObject(sqlStr, Integer.class);
        if (result != null && result == 1) {
            //更新redis内容
            updateRedisContentForOffset(openId, achieveType, jsonObject, achieve.getId());
            insertUserStampAndAchieve(openId, achieve);
        }
    }

    /**
     * 插入用户邮票与成就到数据库
     *
     * @param openId  用户open id
     * @param achieve 存储成就id与任务id的成就对象
     */
    private void insertUserStampAndAchieve(String openId, Achieve achieve) {
        //成就完成，这时候给予成就对应的奖励，添加邮票到数据库，添加成就到数据库，然后缓存中成就参数加1
        achieveMapper.insertSingleNewUserAchieve(new UserAchieve(UUID.randomUUID().toString(), openId, achieve.getId(), new Date(), "0"));
        String achieveUnreadNumKey = "listener:" + openId + ":achieve_unread_num";
        String stampUnreadNumKey = "listener:" + openId + ":stamp_unread_num";
        Integer achieveNum = (Integer) redisUtil.get(achieveUnreadNumKey);
        Integer stampNum = (Integer) redisUtil.get(stampUnreadNumKey);
        redisUtil.set(achieveUnreadNumKey, achieveNum == null ? 1 : achieveNum + 1);

        String stampString = achieveMapper.selectAchieveRewardById(achieve.getId());
        String[] stampIdArray = "".equals(stampString) ? new String[0] : stampString.split(",");
        for (String stampId : stampIdArray) {
            stampMapper.insertSingleNewUserStamp(new UserStamp(UUID.randomUUID().toString(), stampId, openId, "1", new Date(), 1));
        }
        redisUtil.set(stampUnreadNumKey, stampNum == null ? 1 : (stampNum + stampIdArray.length));
        //检测邮票
        //查看邮票是否满足
        if (Boolean.FALSE.equals(STAMP.equals(achieve.getAchieveType()))) {
            //此时不是邮票成就，奖励邮票
            //这里调用消费消息方法，实际应该发布这个邮票消息
            consumeMessage(openId, STAMP, EMPTY_STRING);
        }
    }


    /**
     * 从数据库查询的sql条件，拼接sql语句
     *
     * @param openId  用户open id
     * @param achieve 存储成就id与任务id的成就对象
     * @return 拼接好的sql语句
     */
    private String combineSqlString(String openId, Achieve achieve) {
        //此时根据json串拼接sql语句查询是否满足条件
        String taskSql = taskMapper.selectTaskJsonByTaskId(achieve.getTaskId());
        if (taskSql == null) {
            return "";
        }
        taskSql = taskSql.replace("\"", "").replace("#{openId}", "'" + openId + "'");
        return taskSql;
    }

    /**
     * 更新redis存储偏移量json串
     *
     * @param openId         用户open id
     * @param achieveType    成就类型
     * @param jsonObject     redis中存储用户的成就偏移量json对象
     * @param alreadyExistId 已经获取到的事件ID
     */
    private void updateRedisContentForOffset(String openId, String achieveType, JSONObject jsonObject, String alreadyExistId) {
        @SuppressWarnings("unchecked") ArrayList<String> list = (ArrayList<String>) jsonObject.get(achieveType);
        list.add(alreadyExistId);
        jsonObject.put(achieveType, list);
        redisUtil.set("listener:" + openId + ":offset", jsonObject);
    }


    /**
     * 更新redis中时间线的缓存
     *
     * @param openId    open id
     * @param eventType 事件类型
     */
    private void updateRedisTimeline(String openId, String eventType, String content) {
        if (Boolean.FALSE.equals(LETTER.equals(eventType) || DIARY.equals(eventType) || SPIT.equals(eventType))) {
            return;
        }
        String timelineKey = "user:" + openId + ":timeline";
        @SuppressWarnings("unchecked") LinkedList<TimelineVo> timelineVoLinkedList = (LinkedList<TimelineVo>) redisUtil.get(timelineKey);
        TimelineVo timelineVo = new TimelineVo(openId, eventType, LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), content);
        if (timelineVoLinkedList == null) {
            LinkedList<TimelineVo> timelineVos = new LinkedList<>();
            timelineVos.addFirst(timelineVo);
            redisUtil.set(timelineKey, timelineVos);
        } else {
            timelineVoLinkedList.addFirst(timelineVo);
            redisUtil.set(timelineKey, timelineVoLinkedList);
        }
    }
}
