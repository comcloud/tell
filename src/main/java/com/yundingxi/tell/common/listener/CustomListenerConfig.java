package com.yundingxi.tell.common.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.entity.UserAchieve;
import com.yundingxi.tell.bean.entity.UserStamp;
import com.yundingxi.tell.common.CenterThreadPool;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.AchieveMapper;
import com.yundingxi.tell.mapper.StampMapper;
import com.yundingxi.tell.mapper.TaskMapper;
import com.yundingxi.tell.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    private final ThreadPoolExecutor EXECUTOR = CenterThreadPool.getStampAchievePool();

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AchieveMapper achieveMapper;

    @Autowired
    private StampMapper stampMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 处理保存信件事件
     *
     * @param letterEvent 信件事件
     */
    @EventListener
    public void handleSaveLetterEvent(PublishLetterEvent letterEvent) {
        LOG.info("触发保存信件事件，此时应该更新关于信件的成就内容");
        LOG.info(letterEvent.getLetterStorageDto().toString());
        EXECUTOR.execute(() -> {
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
            String openId = letterEvent.getLetterStorageDto().getOpenId();
            String json = (String) redisUtil.get(openId);
            int locationObtained = JsonUtil.parseJson(json).get("letter").asInt();
            //此时通过获取到的位置以及属于的类型查询成就表然后判断对应的任务是否满足
            List<Achieve> achieveList = achieveMapper.selectAllTaskIdAndIdByAchieveTypeAndLocation(locationObtained, "letter");
            achieveList.forEach(achieve -> {
                String taskJson = taskMapper.selectTaskJsonByTaskId(achieve.getTaskId());
                //此时根据json串拼接sql语句查询是否满足条件
                JsonNode parseJson = JsonUtil.parseJson(taskJson);
                String from = parseJson.get("from").toString();
                String count = parseJson.get("count").toString();
                JsonNode condition = parseJson.get("condition");
                String symbol = parseJson.get("symbol").toString();
                StringBuilder sqlBuilder = new StringBuilder("select count(*) ");
                sqlBuilder.append(symbol).append(count).append(" from ").append(from).append(" where 1=1");
                if (condition.isArray()) {
                    condition.forEach(con -> {
                        String conStr = con.toString();
                        if (conStr.contains("open_id")) {
                            conStr = conStr.replace("#{}", "'" + openId + "'");
                        }
                        sqlBuilder.append(" and ").append(conStr);
                    });
                }
                String sqlStr = sqlBuilder.toString().replace("\"", "");
                Integer result = jdbcTemplate.queryForObject(sqlStr, Integer.class);
                if (result != null && result == 1) {
                    //成就完成，这时候给予成就对应的奖励，添加邮票到数据库，添加成就到数据库，然后缓存中成就参数加1
                    //奖励是一些邮票内容
                    String stampString = achieveMapper.selectAchieveRewardById(achieve.getId());
                    String[] stampIdArray = "".equals(stampString) ? new String[0] : stampString.split(",");
                    for (String stampId : stampIdArray) {
                        stampMapper.insertSingleNewUserStamp(new UserStamp(UUID.randomUUID().toString(), stampId, openId, "1", new Date(), 1));
                    }
                    //添加成就
                    achieveMapper.insertSingleNewUserAchieve(new UserAchieve(UUID.randomUUID().toString(), openId, achieve.getId(), new Date(), "1"));
                }
                //更新redis内容
                ObjectNode objectNode = (ObjectNode) JsonUtil.parseJson(json);
                objectNode.put("letter", locationObtained + 1);
                redisUtil.set(openId,objectNode.toPrettyString());
                //这里就是成就没有完成，那么就是什么都不做
            });
        });
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
        EXECUTOR.execute(() -> {

        });
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
        EXECUTOR.execute(() -> {

        });
    }

}
