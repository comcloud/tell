package com.yundingxi.web.configuration.kafka.achievestamp;

import com.yundingxi.common.model.enums.AchieveStampEnum;
import com.yundingxi.common.util.SpringUtil;
import com.yundingxi.web.configuration.kafka.more.AchieveStampContext;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

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

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        /*
         * 需要根据类型进行分区分配，这里类型就使用key来代替
         * 对分区数取模，可以达到轮训的方式
         * */
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        return AchieveStampEnum.valueOf(key.toString()).getHashCode() % partitionInfos.size();
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
