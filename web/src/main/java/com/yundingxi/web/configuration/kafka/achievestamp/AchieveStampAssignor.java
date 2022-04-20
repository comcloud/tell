package com.yundingxi.web.configuration.kafka.achievestamp;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yundingxi.common.model.constant.CommonConstant;
import com.yundingxi.common.model.enums.AchieveStampEnum;
import com.yundingxi.common.util.SpringUtil;
import com.yundingxi.web.configuration.kafka.more.AchieveStampContext;
import com.yundingxi.web.configuration.kafka.more.KafkaContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
@Slf4j
public class AchieveStampAssignor extends AbstractPartitionAssignor {

    private final AchieveStampContext achieveStampContext =
            (AchieveStampContext) SpringUtil.getBean(AchieveStampContext.class);

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
        //before
        beforeAssign(partitionsPerTopic);

        //on
        AchieveStampContext context = onAssign(partitionsPerTopic, subscriptions);

        //after
        Map<String, List<TopicPartition>> assignment = context.assignment;
        afterAssign(assignment);

        return assignment;
    }

    /**
     * 执行assign时
     */
    private void beforeAssign(Map<String, Integer> partitionsPerTopic) {
        KafkaContext.PARTITIONS_PER_TOPIC.putAll(partitionsPerTopic);
    }

    private AchieveStampContext onAssign(Map<String, Integer> partitionsPerTopic, Map<String, Subscription> subscriptions) {
        AchieveStampContext achieveStampContext = assignContext(partitionsPerTopic);
        /*
         * key转换，从letter -> consumer-letter-17-9f5748ae-0067-4b64-853b-b966199d1990
         * */
        Map<String, List<TopicPartition>> assignment = achieveStampContext.assignment;
        Map<String, List<String>> consumersPerTopic = consumersPerTopic(subscriptions);
        List<String> consumers = consumersPerTopic.get(CommonConstant.ACHIEVE_STAMP_TOPIC);
        consumers.forEach(consumerId -> {
            Map.Entry<String, List<TopicPartition>> removeEntry = assignment.entrySet().stream()
                    .filter(entry -> consumerId.contains(entry.getKey())).findFirst().orElse(null);
            if (Objects.isNull(removeEntry)) {
                return;
            }
            List<TopicPartition> value = removeEntry.getValue();
            assignment.remove(removeEntry.getKey());
            assignment.put(consumerId, value);
        });
        return achieveStampContext;
    }

    /**
     * 执行assign后
     */
    private void afterAssign(Map<String, List<TopicPartition>> assignment) {
        //将结果保存到上下文中
        this.achieveStampContext.assignment.putAll(assignment);
    }

    /**
     * 获取每个主题对应的消费者列表，即[topic, List[consumer]]
     */
    private Map<String, List<String>> consumersPerTopic(Map<String, Subscription> consumerMetadata) {
        Map<String, List<String>> res = Maps.newHashMap();
        for (Map.Entry<String, Subscription> subscriptionEntry : consumerMetadata.entrySet()) {
            String consumerId = subscriptionEntry.getKey();
            for (String topic : subscriptionEntry.getValue().topics()) {
                put(res, topic, consumerId);
            }
        }
        return res;
    }

    /**
     * 处理上下文信息
     * 1.初始化每个topic partition hashCode绑定的类型
     */
    private AchieveStampContext assignContext(Map<String, Integer> partitionsPerTopic) {
        //循环变量
        AtomicInteger numPartitionsForTopic = new AtomicInteger(partitionsPerTopic.get(CommonConstant.ACHIEVE_STAMP_TOPIC));
        //如果分区数小于需要的分区数，说明不够分配，抛出错误日志
        if (numPartitionsForTopic.get() < AchieveStampEnum.values().length - 1) {
            log.error("分区数小于需要的分区数！！！");
        }
        Map<String, List<TopicPartition>> typePartitions = achieveStampContext.assignment;
        //拿到所有的这些topic partition
        List<TopicPartition> partitions = AbstractPartitionAssignor.partitions(CommonConstant.ACHIEVE_STAMP_TOPIC, numPartitionsForTopic.get());
        //最小分区索引，为0
        int minPartitionIndex = 0;
        //循环到分配完成
        while (numPartitionsForTopic.get() >= minPartitionIndex) {
            //将每个位置分配给他的类型
            Arrays.stream(AchieveStampEnum.values())
                    .filter(achieveStampEnum -> Boolean.FALSE.equals(achieveStampEnum.equals(AchieveStampEnum.EMPTY_TYPE)))
                    .forEach(achieveStampEnum -> {
                        if (numPartitionsForTopic.get() < minPartitionIndex) {
                            return;
                        }
                        typePartitions.compute(achieveStampEnum.getGroupId(), (groupId, partitionIndexList) -> {
                            TopicPartition topicPartition = partitions.get(numPartitionsForTopic.getAndIncrement());
                            if (partitionIndexList == null) {
                                return Lists.newArrayList(topicPartition);
                            } else {
                                partitionIndexList.add(topicPartition);
                                return partitionIndexList;
                            }
                        });
                    });
        }
        return achieveStampContext;
    }

    @Override
    public String name() {
        return "成就邮票消费者组分区分配策略";
    }
}
