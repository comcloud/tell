package com.yundingxi.common.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;

/**
 * Redis核心配置类
 *
 */
@Configuration
@Controller
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Value("${spring.redis.database}")
    public String redisDatabaseIndex;

    /**
     * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        om.enableDefaultTyping(DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        // key序列化
        redisTemplate.setKeySerializer(stringSerializer);
        // value序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash key序列化
        redisTemplate.setHashKeySerializer(stringSerializer);
        // Hash value序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 消息监听
     * <p>
     * 可以传入多个 MessageListenerAdapter
     */
    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(@Qualifier("listenerAdapter") MessageListenerAdapter messageListenerAdapter, @Qualifier("listenerAdapter1") MessageListenerAdapter listenerAdapter1) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 监听所有库的key过期
        container.setConnectionFactory(redisConnectionFactory);
        // 监听当前库的key过期
        container.addMessageListener(messageListenerAdapter, new PatternTopic("__keyevent@" + redisDatabaseIndex + "__:expired"));
        // 可以添加多个 messageListener，配置不同的通道
        container.addMessageListener(listenerAdapter1, new PatternTopic("lizhou"));
        return container;
    }

    /**
     * 消息监听器适配器，绑定消息处理器
     * <p>
     * 可以配置多个 listenerAdapter，监听不同的通道
     */
    @Bean
    MessageListenerAdapter listenerAdapter(RedisCurrentKeyExpirationListener receiver) {
        return new MessageListenerAdapter(receiver, "onMessage");
    }

    /**
     * 消息监听器适配器，绑定消息处理器
     * <p>
     * 可以配置多个 listenerAdapter，监听不同的通道
     */
    @Bean
    MessageListenerAdapter listenerAdapter1(RedisMessageListener receiver) {
        return new MessageListenerAdapter(receiver, "onMessage");
    }
}
