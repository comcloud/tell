package com.yundingxi.web.configuration.kafka.achievestamp;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yundingxi.common.model.constant.CommonConstant;
import com.yundingxi.common.model.enums.AchieveStampEnum;
import com.yundingxi.common.util.SpringUtil;
import com.yundingxi.web.configuration.kafka.more.AchieveStampContext;
import com.yundingxi.web.configuration.kafka.more.KafkaContext;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

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

    /**
     * key : 类型
     * value : 类型下消费者已经处理到哪一个位置
     */
    private final Map<String, Integer> map = Maps.newConcurrentMap();

    /**
     * 专门针对成就邮票的分区分配器
     *
     * @param partitionsPerTopic 每个topic下的分区数
     * @param subscriptions      每个consumerId对应的订阅情况，情况中包括订阅分区等等
     * @return consumerId与其消费的分区情况
     */
    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic,
                                                    Map<String, Subscription> subscriptions) {
        KafkaContext.PARTITIONS_PER_TOPIC.putAll(partitionsPerTopic);
        Map<String, List<String>> consumersPerTopic = consumersPerTopic(subscriptions);
        Map<String, List<TopicPartition>> assignment = Maps.newHashMap();
        //初始化assignment
        for (String memberId : subscriptions.keySet()) {
            assignment.put(memberId, Lists.newArrayList());
        }
        consumersPerTopic.forEach((topic, consumers) -> {
            Integer numPartitionsForTopic = partitionsPerTopic.get(topic);
            if (numPartitionsForTopic == null) {
                return;
            }
            List<TopicPartition> partitions = AbstractPartitionAssignor.partitions(topic, numPartitionsForTopic);
            /*
             * 这里有三种情况
             * 1.消费者和分区对等，顺序分配即可
             * 2.消费者偏多，实际上这种情况应该杜绝，因为过多的消费者不做事情也是浪费资源
             * 3.消费者和分区数都多一些，消费者同一类型不止一个
             * 对于3情况是需要考虑的，只是循环则可能每次不同的消费者消费同一分区
             * */
            consumers.forEach(consumerId -> {
                int topicPartitionIndex = map.compute(consumerId, (k, v) -> v == null
                        ? AchieveStampEnum.groupIdOf(consumerId).getPartitionIndex()
                        : v + numPartitionsForTopic - 1);
                assignment.compute(consumerId, (k, v) -> {
                    if (Objects.isNull(v)) {
                        return Lists.newArrayList(partitions.get(topicPartitionIndex));
                    } else {
                        v.add(partitions.get(topicPartitionIndex));
                        return v;
                    }
                });

            });
        });
        //将结果保存到上下文中
        achieveStampContext.typePartitions.putAll(assignment);
        return assignment;
    }

    /**
     * 获取每个主题对应的消费者列表，即[topic, List[consumer]]
     */
    private Map<String, List<String>> consumersPerTopic(Map<String, Subscription> consumerMetadata) {
        Map<String, List<String>> res = Maps.newHashMap();
        for (Map.Entry<String, Subscription> subscriptionEntry : consumerMetadata.entrySet()) {
            String consumerId = transformConsumerId(subscriptionEntry.getKey());
            for (String topic : subscriptionEntry.getValue().topics()) {
                put(res, topic, consumerId);
            }
        }
        return res;
    }

    /**
     * @param consumerId consumer-spit-17-9f5748ae-0067-4b64-853b-b966199d1990
     * @return spit
     */
    private String transformConsumerId(String consumerId) {
        if (consumerId.contains(CommonConstant.DIARY)) {
            return CommonConstant.DIARY;
        } else if (consumerId.contains(CommonConstant.LETTER)) {
            return CommonConstant.LETTER;
        } else if (consumerId.contains(CommonConstant.SPIT)) {
            return CommonConstant.SPIT;
        } else if (consumerId.contains(CommonConstant.REPLY)) {
            return CommonConstant.REPLY;
        } else {
            return CommonConstant.EMPTY_STRING;
        }
    }

    @Override
    public String name() {
        return "成就邮票消费者组分区分配策略";
    }
}
