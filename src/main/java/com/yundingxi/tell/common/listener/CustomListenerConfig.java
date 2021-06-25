package com.yundingxi.tell.common.listener;

import com.alibaba.fastjson.JSONObject;
import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.entity.UserAchieve;
import com.yundingxi.tell.bean.entity.UserStamp;
import com.yundingxi.tell.bean.vo.TimelineVo;
import com.yundingxi.tell.common.CenterThreadPool;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.AchieveMapper;
import com.yundingxi.tell.mapper.StampMapper;
import com.yundingxi.tell.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 这里执行
 *
 * @version v1.0
 * @ClassName CustomListenerConfig
 * @Author rayss
 * @Datetime 2021/5/12 5:01 下午
 */

@Configuration
public class CustomListenerConfig {

    private final Logger LOG = LoggerFactory.getLogger(CustomListenerConfig.class);

    /**
     * 线程池
     */
    private final ThreadPoolExecutor EXECUTOR = CenterThreadPool.getSTAMP_ACHIEVE_POOL();

    private final RedisUtil redisUtil;

    private final AchieveMapper achieveMapper;

    private final StampMapper stampMapper;

    private final TaskMapper taskMapper;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomListenerConfig(RedisUtil redisUtil, AchieveMapper achieveMapper, StampMapper stampMapper, TaskMapper taskMapper, JdbcTemplate jdbcTemplate) {
        this.redisUtil = redisUtil;
        this.achieveMapper = achieveMapper;
        this.stampMapper = stampMapper;
        this.taskMapper = taskMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 处理保存信件事件
     *
     * @param letterEvent 信件事件
     */
    @EventListener
    public void handleSaveLetterEvent(PublishLetterEvent letterEvent) {
        LOG.info("触发保存信件事件，此时应该更新关于信件的成就内容");
        LOG.info(letterEvent.getLetterStorageDto().toString());
        EXECUTOR.execute(getRunnable(letterEvent.getLetterStorageDto().getOpenId(), "letter", letterEvent.getLetterStorageDto().getContent()));
    }

    /**
     * 处理保存日记事件
     *
     * @param diaryEvent 日记事件
     */
    @EventListener
    public void handleSaveDiary(PublishDiaryEvent diaryEvent) {
        LOG.info("触发保存日记事件，此时应该更新关于日记的成就内容");
        LOG.info(diaryEvent.getDiaryDto().toString());
        EXECUTOR.execute(getRunnable(diaryEvent.getDiaryDto().getOpenId(), "diary", diaryEvent.getDiaryDto().getContent()));
    }

    /**
     * 处理保存吐槽事件
     *
     * @param spitEvent 吐槽事件
     */
    @EventListener
    public void handleSaveSpit(PublishSpitEvent spitEvent) {
        LOG.info("触发保存吐槽事件，此时应该更新关于吐槽的成就内容");
        LOG.info(spitEvent.getSpittingGrooves().toString());
        EXECUTOR.execute(getRunnable(spitEvent.getSpittingGrooves().getOpenId(), "spit", spitEvent.getSpittingGrooves().getContent()));
    }

    @EventListener
    public void handleReply(PublishReplyEvent replyEvent) {
        LOG.info("触发保存回信事件，此时应该更新关于回信的成就内容");
        LOG.info(replyEvent.getLetterReplyDto().toString());
        EXECUTOR.execute(getRunnable(replyEvent.getLetterReplyDto().getRecipient(), "reply", replyEvent.getLetterReplyDto().getMessage()));
    }

    public void handleStampAchieve(String openId) {
        LOG.info("触发邮票事件，此时应该更新关于邮票的成就内容");
        EXECUTOR.execute(getRunnable(openId, "stamp", ""));
    }

    private Runnable getRunnable(String openId, String eventType, String content) {
        return () -> {
            /*
              这时候要做的事情
              letter
              1.根据成就的类型achieve_type来获取对应成就已经获取到的位置
                - 位置每个人都默认是1（不一定在数据库第一位，只是每个类型的第一位），也就是从第一个开始
              2.根据获取到的成就位置查询对应的任务JSON
              3.读取JSON拼接为sql语句查询数据库判断是否已经完成此任务
               - 完成返回true，表示完成的话，需要将位置+1，同时给予对应的奖励achieve_reward，也就是对应的邮票
               - 未完成返回false，什么都不做
              */
            updateRedisTimeline(openId, eventType, content);
            JSONObject jsonObject = (JSONObject) redisUtil.get("listener:" + openId + ":offset");
            @SuppressWarnings("unchecked") ArrayList<String> list = (ArrayList<String>) jsonObject.get(eventType);
            //此时通过获取到的位置以及属于的类型查询成就表然后判断对应的任务是否满足
            List<Achieve> achieveList = achieveMapper.selectAllTaskIdAndIdByAchieveTypeAndNonId(list, eventType);
            achieveList.forEach(achieve -> judgeAchieve(openId, eventType, (JSONObject) redisUtil.get("listener:" + openId + ":offset"), achieve));
        };
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
            return ;
        }
        Integer result = jdbcTemplate.queryForObject(sqlStr, Integer.class);
        if (result != null && result == 1) {
            //更新redis内容
            updateRedisContent(openId, achieveType, jsonObject, achieve.getId());
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
        achieveMapper.insertSingleNewUserAchieve(new UserAchieve(UUID.randomUUID().toString(), openId, achieve.getId(), new Date(), "1"));
        String achieveUnreadNumKey = "listener:" + openId + ":achieve_unread_num";
        String stampUnreadNumKey = "listener:" + openId + ":stamp_unread_num";
        Integer achieveNum = (Integer) redisUtil.get(achieveUnreadNumKey);
        Integer stampNum = (Integer) redisUtil.get(stampUnreadNumKey);
        redisUtil.set(achieveUnreadNumKey, achieveNum == null ? 1 : achieveNum + 1);
        //奖励是一些邮票内容
        String nonAchieveType = "stamp";
        if (nonAchieveType.equals(achieve.getAchieveType())) {
            //此时是邮票成就，不再奖励邮票
            return;
        }
        String stampString = achieveMapper.selectAchieveRewardById(achieve.getId());
        String[] stampIdArray = "".equals(stampString) ? new String[0] : stampString.split(",");
        for (String stampId : stampIdArray) {
            stampMapper.insertSingleNewUserStamp(new UserStamp(UUID.randomUUID().toString(), stampId, openId, "1", new Date(), 1));
        }
        redisUtil.set(stampUnreadNumKey, stampNum == null ? 1 : (stampNum + stampIdArray.length));
        handleStampAchieve(openId);
        //添加成就
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
        if(taskSql == null) {
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
    private void updateRedisContent(String openId, String achieveType, JSONObject jsonObject, String alreadyExistId) {
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
        String nonAchieveType = "stamp";
        if (nonAchieveType.equals(eventType)) {
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
