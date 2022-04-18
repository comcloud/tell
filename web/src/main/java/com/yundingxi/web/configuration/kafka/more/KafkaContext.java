package com.yundingxi.web.configuration.kafka.more;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * kafka上下文
 * @version v1.0
 * @ClassName KafkaContext
 * @Author rayss
 * @Datetime 2022/4/18 10:38 上午
 */
public interface KafkaContext {

    /**
     * 每个topic下的分区数量
     */
    Map<String, Integer> PARTITIONS_PER_TOPIC = Maps.newConcurrentMap();
}
