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
