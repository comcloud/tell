package com.yundingxi.web.configuration.kafka;

import com.yundingxi.common.model.constant.CommonConstant;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName AchieveStampAss
 * @Author rayss
 * @Datetime 2022/4/17 9:56 下午
 */

public class AchieveStampAssignor extends AbstractPartitionAssignor {

    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic,
                                                    Map<String, Subscription> subscriptions) {
        Subscription subscription = subscriptions.get(CommonConstant.ACHIEVE_STAMP_TOPIC);
        List<TopicPartition> topicPartitions = subscription.ownedPartitions();
        

        return null;
    }

    @Override
    public String name() {
        return null;
    }
}
