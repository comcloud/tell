package com.yundingxi.common.model.enums.redis;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/25-9:14
 */
public interface RedisEaseEnums {
    /**
     * 获取几号数据库
     * @return
     */
    int getRedisDbIndex();

    /**
     * 获取key
     * @return
     */
    String getRedisKey();
}
