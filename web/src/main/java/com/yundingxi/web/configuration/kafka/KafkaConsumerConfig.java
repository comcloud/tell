package com.yundingxi.web.configuration.kafka;

import com.yundingxi.web.configuration.kafka.achievestamp.AchieveStampAssignor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 对于消费者工厂的创建方式需要注意一点，其实对于不同类型的消费者他们的消费者配置应该是不同的，所以这里可以做出优化
 * 优化点其实就是将消费者配置抽离出去，然后其他可以用于不变的内容放出去就行，得以复用
 * @version v1.0
 * @ClassName KafkaConsumerConfig
 * @Author rayss
 * @Datetime 2022/4/16 9:33 下午
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Value("${kafka.consumer.auto-commit-interval}")
    private Integer autoCommitInterval;

    @Value("${kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${kafka.listener.concurrencys}")
    private Integer concurrency;

    @Value("${kafka.listener.poll-timeout}")
    private Long pollTimeout;

    @Value("${kafka.consumer.session-timeout}")
    private String sessionTimeout;

    @Value("${kafka.listener.batch-listener}")
    private Boolean batchListener;

    @Value("${kafka.consumer.max-poll-interval}")
    private Integer maxPollInterval;

    @Value("${kafka.consumer.max-partition-fetch-bytes}")
    private Integer maxPartitionFetchBytes;

    @Value("${kafka.consumer.group-id}")
    private String groupId;


    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(20);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //使用成就邮票分区分配策略
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, AchieveStampAssignor.class);

        return props;
    }


    /**
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "kafkaBatchListener")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaBatchListener() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //批量消费
        factory.setBatchListener(batchListener);
        //如果消息队列中没有消息，等待timeout毫秒后，调用poll()方法。
        // 如果队列中有消息，立即消费消息，每次消费的消息的多少可以通过max.poll.records配置。
        //手动提交无需配置
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        //设置提交偏移量的方式， MANUAL_IMMEDIATE 表示消费一条提交一次；MANUAL表示批量提交一次
        //低版本的spring-kafka，ackMode需要引入AbstractMessageListenerContainer.AckMode.MANUAL
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setConcurrency(concurrency);
        return factory;
    }

    private ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

}
