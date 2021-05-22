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



