package com.yundingxi.biz.infrastructure.mq;

import com.yundingxi.common.model.enums.AchieveStampEnum;
import com.yundingxi.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * 消息发送
 *
 * @author rayss
 */
@Component
@Slf4j
public class KafkaProducer<T> {

    private final KafkaTemplate<String, String> KAFKA_TEMPLATE;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.KAFKA_TEMPLATE = kafkaTemplate;
    }

    public void sendMessage(String topic, AchieveStampEnum key, T message) {
        log.info("Send msg:{}", message);

        //结果是一个Future
        ListenableFuture<SendResult<String, String>> sender =
                KAFKA_TEMPLATE.send(topic, key.getGroupId(), JsonUtil.toJsonString(message));
        sender.addCallback(
                result -> {
                    assert result != null;
                    log.info("Send success:offset({}),partition({}),topic({})",
                            result.getRecordMetadata().offset(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().topic());
                },
                ex -> log.error("Send fail:{}", ex.getMessage()));
    }
}
