package com.yundingxi.tell.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis 消息接收
 *
 **/
@Slf4j
@Component
public class RedisMessageListener implements MessageListener {

    /**
     * 消息接收
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 接收的topic
        log.info("channel:" + new String(pattern));
        // 消息的POJO
        log.info("message:" + message.toString());
    }
}
