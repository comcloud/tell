# tell

信件小程序

### `redis`存储`key`

| 存储类                 | key名                                                        | 存储内容                                                     |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `ResourceInit`         | `"listener:" + openId + ":offset"`                           | 保存每个人的邮票成就已经获取的偏移量                         |
| `LetterServiceImpl`    | `"letter:" + openId + ":letter_info"`                        | 每个人的信件内容，包括上次访问时间，上次访问时数据库内容的数量已经信件的List |
| `LetterServiceImpl`    | `"letter:" + openId +"_reserve:reply"`                       | 保存历史信件内容，属于历史回信内容                           |
| `LetterServiceImpl`    | `"letter:" + openId + ":unread_message"`                     | 回复信件未读消息                                             |
| `CustomListenerConfig` | `"user:" + openId + ":timeline"`                             | 每个用户的时间线内容                                         |
| `CustomListenerConfig` | `"listener:" + openId + ":achieve_unread_num"`               | 未读成就数量                                                 |
| `CustomListenerConfig` | `"listener:" + openId + ":stamp_unread_num"`                 | 未读邮票数量                                                 |
| `UserServiceImpl`      | `RedisEnums.USER_DATA_ANALYSIS_MODEL.getRedisKey() + "_" + openId + ":data"` | 用户数据分析内容                                             |

### 获取信件解决方案

**准则**

- 最新信件优先
- 被回复信件比较少的优先
- 随机，每个用户获取的不完全相同
- 单个用户获取到的信件不可以重复
- 不会收到自己发出的解忧信件

#### 方案一：构建哈夫曼树

**使用逻辑是通过信件创建一个哈夫曼树，权重比值参考每封信的回复量以及时间，尽量保证回复信件比较少的以及时间比较新的更容易被获取到，所以在每个用户保存信件、回复信件时候都会更新哈夫曼树**

#### 方案二：计算随机数以及手动保存权重值获取信件

- 通过一个算法计算出来一个阈值，这个阈值就是前多少封为最新信件

  阈值：多少封是最新的，这样按照时间降序，最多会获取到阈值位置的信件

- 缓存中记录着n个最新信件id以及其对应的被回复的数量

- 自旋获取三个不同的数字，这个数字不同指的对阈值取余之后的值不同，不过此处使用`random.nextInt(阈值)`，同时需要将缓存中的权重考虑进去

- 根据三个不同的数字获取对应索引位置的三封信件



##### 计算阈值算法

**考虑因素**

- 当前时间
- 数据库中数据的时间
## 补充，有一些设计模式简单使用
[设计模式讲解](./DesignPatternmd)



## 成就邮票MQ
**这里有两种消费者处理的方式**

1. 使用一个消费者单独处理这个内容，需要对不同类型的消息进行判断，如果是信件则处理信件消息等等
2. 使用多个消费者处理，这些消费者同时订阅这一个topic，监听到的时候需要对消息是否是自己要处理的进行判断
3. 计算机统一解决方式，加入中间层，也就是这里我们想要结合两种方案的有点，我们对于消息、分区等使用第一种方案
    然后加入中间层根据不同类型使用不同的策略

| 方案   | 优点                                                       | 缺点                                                         |
| ------ | ---------------------------------------------------------- | ------------------------------------------------------------ |
| 方案一 | 实现比较直接简单，只需要定义好消费者还有topic              | 需要在消费者中添加对不同情况的逻辑判断，对于以后需要新增等需要改动原有逻辑代码，不符合我们的开发原则 |
| 方案二 | 利于水平扩展，不同的分区作用不同，不同分区用不同消费者处理 | 实现比较复杂，生产者分区策略还有消费者消费策略需要自定义，同时还需要考虑某一类型分区节点宕机，那么对应某一类型消息没有办法消费<br />目前实现比较鸡肋，是因为分区需要绑定类型，消费者也是如此 |
| 方案三 | 实现简单，同时水平扩展能力也强 | 暂无 | 
不过为了方便扩展等特性，使用方案二，方案二存在如下问题
- 如果某个类型的分区节点宕机则消息没有办法处理 
>所以每个类型提供多个分区，进而降低问题出现的可能性

**多个分区和多个消费者如何处理**
> 自定义分区分配策略
- 生产者策略代码

按照顺序依次发送给目标节点
  
~~~java
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

}
>   ~~~
- 消费者策略代码

同样，按照顺序分配
~~~java
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

~~~

