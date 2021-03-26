package com.yundingxi.tell.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis 消息接收
 *
 */
@Slf4j
@Component
public class RedisCurrentKeyExpirationListener implements MessageListener {

    /**
     * 针对 redis 数据失效事件，进行数据处理
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 拿到key
        log.info("监听Redis当前库 key过期，key：{}，channel：{}", message.toString(), new String(pattern));
    }
}