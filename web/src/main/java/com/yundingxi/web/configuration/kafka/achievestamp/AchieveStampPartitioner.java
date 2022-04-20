package com.yundingxi.web.configuration.kafka.achievestamp;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yundingxi.common.util.SpringUtil;
import com.yundingxi.web.configuration.kafka.more.AchieveStampContext;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;

/**
 * 成就邮票分区分配
 *
 * @version v1.0
 * @ClassName AchieveStampPartitioner
 * @Author rayss
 * @Datetime 2022/4/18 10:47 上午
 */

public class AchieveStampPartitioner implements Partitioner {

    private final Map<String, Integer> map = Maps.newConcurrentMap();

    private final AchieveStampContext achieveStampContext = (AchieveStampContext) SpringUtil.getBean(AchieveStampContext.class);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        /*
        * 从绑定的上下文拿到对应负责的分区，然后轮训发往分区
        * */
        Map<String, List<TopicPartition>> assignment = achieveStampContext.assignment;
        List<TopicPartition> topicPartitions = assignment.entrySet().stream()
                .filter(entry -> entry.getKey().contains(key.toString()))
                .map(Map.Entry::getValue).findFirst().orElse(Lists.newArrayList());

        return topicPartitions.get(
                map.compute(key.toString(), (k, v) -> v == null ? 0 : (v + 1) % topicPartitions.size())
        ).partition();
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
