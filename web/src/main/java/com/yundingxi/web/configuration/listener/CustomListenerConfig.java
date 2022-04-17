package com.yundingxi.web.configuration.listener;

import com.alibaba.fastjson.JSONObject;
import com.yundingxi.common.redis.RedisUtil;
import com.yundingxi.dao.mapper.AchieveMapper;
import com.yundingxi.dao.mapper.LetterMapper;
import com.yundingxi.dao.mapper.StampMapper;
import com.yundingxi.dao.mapper.TaskMapper;
import com.yundingxi.dao.model.*;
import com.yundingxi.model.dto.Diary.DiaryDto;
import com.yundingxi.model.dto.letter.LetterReplyDto;
import com.yundingxi.model.dto.letter.LetterStorageDto;
import com.yundingxi.model.vo.IndexLetterVo;
import com.yundingxi.model.vo.TimelineVo;
import com.yundingxi.web.component.ResourceInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
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
    @Resource
    private ThreadPoolExecutor stampAchievePool;

    private final RedisUtil redisUtil;

    private final AchieveMapper achieveMapper;

    private final StampMapper stampMapper;

    private final TaskMapper taskMapper;

    private final JdbcTemplate jdbcTemplate;

    private final ResourceInit resourceInit;

    private final LetterMapper letterMapper;

    @Autowired
    public CustomListenerConfig(RedisUtil redisUtil, AchieveMapper achieveMapper, StampMapper stampMapper, TaskMapper taskMapper, JdbcTemplate jdbcTemplate, ResourceInit resourceInit, LetterMapper letterMapper) {
        this.redisUtil = redisUtil;
        this.achieveMapper = achieveMapper;
        this.stampMapper = stampMapper;
        this.taskMapper = taskMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.resourceInit = resourceInit;
        this.letterMapper = letterMapper;
    }

    /**
     * 处理保存信件事件
     *
     * @param letterEvent 信件事件
     */
    @EventListener
    public void handleSaveLetterEvent(UserBehaviorEvent<LetterStorageDto> letterEvent) {
        LOG.info("触发保存信件事件，此时应该更新关于信件的成就内容");
        LOG.info(letterEvent.getT().toString());
        stampAchievePool.execute(getRunnable(letterEvent.getT().getOpenId(), "letter", letterEvent.getT().getContent()));
    }

    /**
     * 处理保存日记事件
     *
     * @param diaryEvent 日记事件
     */
    @EventListener
    public void handleSaveDiary(UserBehaviorEvent<DiaryDto> diaryEvent) {
        LOG.info("触发保存日记事件，此时应该更新关于日记的成就内容");
        LOG.info(diaryEvent.getT().toString());
        stampAchievePool.execute(getRunnable(diaryEvent.getT().getOpenId(), "diary", diaryEvent.getT().getContent()));
    }

    /**
     * 处理保存吐槽事件
     *
     * @param spitEvent 吐槽事件
     */
    @EventListener
    public void handleSaveSpit(UserBehaviorEvent<SpittingGrooves> spitEvent) {
        LOG.info("触发保存吐槽事件，此时应该更新关于吐槽的成就内容");
        LOG.info(spitEvent.getT().toString());
        stampAchievePool.execute(getRunnable(spitEvent.getT().getOpenId(), "spit", spitEvent.getT().getContent()));
    }

    @EventListener
    public void handleReply(UserBehaviorEvent<LetterReplyDto> replyEvent) {
        LOG.info("触发保存回信事件，此时应该更新关于回信的成就内容");
        LOG.info(replyEvent.getT().toString());
        //处理用户回复信件的行为
        LetterReplyDto replyDto = replyEvent.getT();
        updateRedisContentForUserBehavior(replyDto.getSender(), replyDto.getLetterId());
        //处理用户回复信件对成就邮票的影响
        stampAchievePool.execute(getRunnable(replyDto.getRecipient(), "reply", replyDto.getMessage()));
    }


    public void handleStampAchieve(String openId) {
        LOG.info("触发邮票事件，此时应该更新关于邮票的成就内容");
        stampAchievePool.execute(getRunnable(openId, "stamp", ""));
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
            if (list == null) {
                resourceInit.stampAndAchieveInitForEveryone(openId, true);
                jsonObject = (JSONObject) redisUtil.get("listener:" + openId + ":offset");
                list = (ArrayList<String>) jsonObject.get(eventType);
            }
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
        String nonAchieveType = "stamp";
        if (!nonAchieveType.equals(achieve.getAchieveType())) {
            //此时不是邮票成就，奖励邮票
            handleStampAchieve(openId);
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
     * 更新redis缓存中的用户行为内容，其中存储的键值对分别是，标签名：数量
     *
     * @param openId     用户open id
     * @param jsonObject 存储数据的JSON对象
     * @param tapNames   标签名数组
     */
    private void updateRedisContentForUserBehavior(String openId, JSONObject jsonObject, String... tapNames) {
        String userBehaviorKey = "listener:" + openId + ":userBehavior";
        for (String tapName : tapNames) {
            if (jsonObject == null || jsonObject.isEmpty()) {
                //初始化缓存说明此时这个用户没有进行过行为操作
                JSONObject newJsonObject = new JSONObject();
                newJsonObject.put(tapName, 1);
                jsonObject = newJsonObject;
                redisUtil.set(userBehaviorKey, newJsonObject);
            } else {
                Object o = jsonObject.get(tapName);
                int tapNameCount = o == null ? 1 : ((Integer) o) + 1;
                jsonObject.put(tapName, tapNameCount);
                redisUtil.set(userBehaviorKey, jsonObject);
            }
        }
    }

    /**
     * 更新redis中时间线的缓存
     *
     * @param openId    open id
     * @param eventType 事件类型
     */
    private void updateRedisTimeline(String openId, String eventType, String content) {
        String isAchieveType = "letter,spit,diary";
        if (!isAchieveType.contains(eventType)) {
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


    //------------------------- 用户行为监听 ----------------------

    @EventListener
    public void handleGetLetterBehavior(UserBehaviorEvent<IndexLetterVo> userBehaviorEvent){
        IndexLetterVo indexLetterVo = userBehaviorEvent.getT();
        updateRedisContentForUserBehavior(indexLetterVo.getOpenId(),indexLetterVo.getLetterId());
    }

    /**
     * 更新用户行为到redis缓存中，传入信件id用于获取标签
     * @param openId 用户open id
     * @param letterId letter id
     */
    private void updateRedisContentForUserBehavior(String openId, String letterId) {
        Letter letter = letterMapper.selectLetterById(letterId);
        String[] tapIds = letter.getTapIds().split(",");
        updateRedisContentForUserBehavior(openId, (JSONObject) redisUtil.get("listener:" + openId + ":userBehavior"), tapIds);
    }
}
