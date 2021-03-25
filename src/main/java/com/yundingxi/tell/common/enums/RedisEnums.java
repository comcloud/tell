package com.yundingxi.tell.common.enums;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * @author hds
 * <p>项目名称: Redis常量
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/25-9:13
 */
@AllArgsConstructor
public enum RedisEnums implements RedsiBaseEnums {
    /**
     * redis 系统美文 的redis 数据库号，以及key 值
     */
    SYS_BEAUTYWEN_HASHCODE(2,"system:beautyWen:hashCode");

    @Setter
    private int dbIndex;
    @Setter
    private String key;


    @Override
    public int getRedisDbIndex() {
        return dbIndex;
    }

    @Override
    public String getRedisKey() {
        return key;
    }
}
