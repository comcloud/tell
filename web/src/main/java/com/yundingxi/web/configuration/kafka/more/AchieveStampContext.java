package com.yundingxi.web.configuration.kafka.more;

import com.google.common.collect.Maps;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 成就邮票上下文，关于成就邮票消息的额外内容可以放到这里
 *
 * @version v1.0
 * @ClassName AchieveStampContext
 * @Author rayss
 * @Datetime 2022/4/18 10:38 上午
 */
@Component
public class AchieveStampContext implements KafkaContext {

    /**
     * key : type : letter etc.
     * value : 此类型的分区都有哪些
     */
    public Map<String, List<TopicPartition>> assignment = Maps.newConcurrentMap();

    /**
     * 绑定分区和类型关系
     * value : topic partition hashCodes
     */
    public Map<String, List<Integer>> hashCodeTypeMap = Maps.newHashMap();
}
