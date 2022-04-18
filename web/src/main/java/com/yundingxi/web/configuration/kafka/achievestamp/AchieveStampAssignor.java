package com.yundingxi.web.configuration.kafka.achievestamp;

import com.google.common.collect.Maps;
import com.yundingxi.common.model.constant.CommonConstant;
import com.yundingxi.common.model.enums.AchieveStampEnum;
import com.yundingxi.common.util.SpringUtil;
import com.yundingxi.web.configuration.kafka.more.AchieveStampContext;
import com.yundingxi.web.configuration.kafka.more.KafkaContext;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.support.KafkaUtils;

import java.util.List;
import java.util.Map;

/**
 * 自定义消费者组策略
 * 需要实现的功能就是：对于某个消费者而言，获取这个消费者的类型去消费某个分区
 * Map<String, List<Integer>>，消费者类型对应他的分区
 * 这个对于生产者的分区分配策略比较容易实现，只需要设置一个全局map如上，然后生产者分区分配之后将这个类型的分区添加到map中、
 * 对于这个map的管理我们考虑放到IOC容器中
 *
 * @version v1.0
 * @ClassName AchieveStampAss
 * @Author rayss
 * @Datetime 2022/4/17 9:56 下午
 */

public class AchieveStampAssignor extends AbstractPartitionAssignor {

    private final AchieveStampContext achieveStampContext =
            (AchieveStampContext) SpringUtil.getBean(AchieveStampContext.class);

    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic,
                                                    Map<String, Subscription> subscriptions) {
        KafkaContext.PARTITIONS_PER_TOPIC.putAll(partitionsPerTopic);
        Map<String, List<TopicPartition>> typePartitions = Maps.newHashMap();

        Subscription subscription = subscriptions.get(CommonConstant.ACHIEVE_STAMP_TOPIC);
        List<TopicPartition> topicPartitions = subscription.ownedPartitions();

        String consumerGroupId = KafkaUtils.getConsumerGroupId();
        AchieveStampEnum.valueOf(consumerGroupId).getHashCode() % topicPartitions.size();

        return null;
    }

    /**
     * 获取每个主题对应的消费者列表，即[topic, List[consumer]]
     */
    private Map<String, List<String>> consumersPerTopic(Subscription consumerMetadata) {
        Map<String, List<String>> res = Maps.newHashMap();
        String consumerId = CommonConstant.ACHIEVE_STAMP_TOPIC;
        for (String topic : consumerMetadata.topics()) {
            put(res, topic, consumerId);
        }
        return res;
    }

    @Override
    public String name() {
        return null;
    }
}
