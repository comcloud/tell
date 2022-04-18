package com.yundingxi.web.configuration.kafka.achievestamp;

import com.google.common.collect.Maps;
import com.yundingxi.common.model.enums.AchieveStampEnum;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

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

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        /*
         * 需要根据类型进行分区分配，这里类型就使用key来代替
         * 对分区数取模，可以达到轮训的方式
         * 需要注意的是，我们需要做到一种情况，就是记录每次此时到哪一个分区
         * 换句话来说就是，letter本次是0，下次如果分区允许应该是下一个位置
         * 5个分区，4个类型
         * */
        Integer partitionCountForTopic = cluster.partitionCountForTopic(topic);
        return map.compute(
                key.toString(), (k, v) -> v == null
                        ? AchieveStampEnum.valueOf(key.toString()).getPartitionIndex()
                        : v + partitionCountForTopic - 1);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
