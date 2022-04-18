package com.yundingxi.biz.infrastructure.mq;

import com.yundingxi.biz.model.AchieveStampMessage;
import com.yundingxi.common.model.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.yundingxi.common.model.constant.CommonConstant.*;

/**
 * 消息接收
 *
 * @author rayss
 */
@Component
@Slf4j
public class KafkaConsumer {

    /**
     * containerFactory:定义批处理器，批处理消费的线程数由kafka.listener.concurrencys控制
     * topics：消费的消息队列的topic
     */
    @KafkaListener(containerFactory = "kafkaBatchListener", id = LETTER_GROUP_ID, topics = {CommonConstant.ACHIEVE_STAMP_TOPIC})
    public void letterAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends AchieveStampMessage<?>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());

            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }

    }

    @KafkaListener(containerFactory = "kafkaBatchListener", id = DIARY_GROUP_ID, topics = {CommonConstant.ACHIEVE_STAMP_TOPIC})
    public void diaryAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends AchieveStampMessage<?>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());

            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }

    }

    @KafkaListener(containerFactory = "kafkaBatchListener", id = SPIT_GROUP_ID, topics = {CommonConstant.ACHIEVE_STAMP_TOPIC})
    public void spitAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends AchieveStampMessage<?>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());

            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }

    }

    @KafkaListener(containerFactory = "kafkaBatchListener", id = REPLY_GROUP_ID, topics = {"achieveAndStamp"})
    public void replyAchieveAndStampConsumer(List<ConsumerRecord<?, ? extends AchieveStampMessage<?>>> records, Acknowledgment ack) {

        try {
            records.forEach(record -> {
                log.info("receive {} msg:{}", record.topic(), record.value().toString());

            });
        } catch (Exception e) {
            log.error("kafka listen error:{}", e.getMessage());

        } finally {
            //手动提交偏移量
            ack.acknowledge();
        }

    }


    @Bean
    public PartitionFinder finder(ConsumerFactory<String, String> consumerFactory) {
        return new PartitionFinder(consumerFactory);
    }

    public static class PartitionFinder {

        private final ConsumerFactory<String, String> consumerFactory;

        public PartitionFinder(ConsumerFactory<String, String> consumerFactory) {
            this.consumerFactory = consumerFactory;
        }

        public String[] partitions(String topic) {
            try (Consumer<String, String> consumer = consumerFactory.createConsumer()) {
                return consumer.partitionsFor(topic).stream()
                        .map(pi -> "" + pi.partition())
                        .toArray(String[]::new);
            }
        }

    }
}
