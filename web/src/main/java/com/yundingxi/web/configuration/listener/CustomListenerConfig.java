package com.yundingxi.web.configuration.listener;

import com.alibaba.fastjson.JSONObject;
import com.yundingxi.biz.model.UserBehaviorEvent;
import com.yundingxi.common.redis.RedisUtil;
import com.yundingxi.dao.mapper.LetterMapper;
import com.yundingxi.dao.model.Letter;
import com.yundingxi.model.vo.IndexLetterVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

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

    private final RedisUtil redisUtil;

    private final LetterMapper letterMapper;

    @Autowired
    public CustomListenerConfig(RedisUtil redisUtil, LetterMapper letterMapper) {
        this.redisUtil = redisUtil;
        this.letterMapper = letterMapper;
    }


    //------------------------- 用户行为监听 ----------------------

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

    @EventListener
    public void handleGetLetterBehavior(UserBehaviorEvent<IndexLetterVo> userBehaviorEvent) {
        IndexLetterVo indexLetterVo = userBehaviorEvent.getT();
        updateRedisContentForUserBehavior(indexLetterVo.getOpenId(), indexLetterVo.getLetterId());
    }

    /**
     * 更新用户行为到redis缓存中，传入信件id用于获取标签
     *
     * @param openId   用户open id
     * @param letterId letter id
     */
    private void updateRedisContentForUserBehavior(String openId, String letterId) {
        Letter letter = letterMapper.selectLetterById(letterId);
        String[] tapIds = letter.getTapIds().split(",");
        updateRedisContentForUserBehavior(openId, (JSONObject) redisUtil.get("listener:" + openId + ":userBehavior"), tapIds);
    }
}
