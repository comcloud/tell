package com.yundingxi.biz.model;

import java.io.Serializable;

/**
 * 这里有两种消费者处理的方式
 * 1.使用一个消费者单独处理这个内容，需要对不同类型的消息进行判断，如果是信件则处理信件消息等等
 * 2.使用多个消费者处理，这些消费者同时订阅这一个topic，监听到的时候需要对消息是否是自己要处理的进行判断
 * 这里使用第二种方案
 * 如果使用一个消费者处理不利于水平扩展，如果下次有新的监听事件需要更改代码，加上判断，不符合我们的开发原则
 * 另外使用多个消费者处理也可以提高消息的并发度和吞吐量，防止所有消息都打到一台机器上，这些不同的消费者我们可以放到不同的机器进行负载
 * 对于第二种方案 ： 使用多个消费者处理，每个消费者处理多个同类型分区，但是如此需要添加新的消息时候添加分区以及新的消费者
 * 成就与邮票mq消息
 *
 * 但是使用第二种方案需要注意一个点就是，创建比较多的消费者，他们如果属于一个消费者组，那么这条消息本来该letter消费者处理的，但是
 * 却分配给了其他消费者，导致消息没有处理到位，所以这里我们可以使用自定义分配方式，根据类型进行消费者分配，不过固定类型消息要固定发往固定分区
 * 然后分区指定消费者组中的某个消费者进行消费，不过这样处理的话就一定是存在这样的关系
 * letter partition<->letter consumer
 * consumer数量一定<=对应类型分区数量，当然consumer可以是多个
 * ----------------------------------------------------------------------------------------
 * 第二种方案缺点如何，之所以会有这个问题原因是每个监听事件需要跟某个partition绑定，如果新增事件则需要新增partition
 * 之所以需要跟分区绑定，是因为想要将指定分区交给某一特定消费者消费，这样消费者消费时候就不需要再做判断
 * [__________]
 *
 * @version v1.0
 * @ClassName AchieveStampMessage
 * @Author rayss
 * @Datetime 2022/4/17 9:40 下午
 */

public class AchieveStampMessage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T object;

    public Class<?> getObjectType() {
        return object.getClass();
    }
}
