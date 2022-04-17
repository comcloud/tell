package com.yundingxi.common.model.enums.redis;

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
public enum RedisEnums implements RedisEaseEnums {
    /**
     * redis 系统美文 的redis 数据库号，以及key 值
     */
    SYS_BEAUTYWEN_HASHCODE(2,"system:beautyWen:hashCode"),

    SYS_BEAUTYWEN_JSONS(2,"system:beautyWen:JSONS"),
    SYS_BEAUTYWEN_HOME_JSONS(2,"system:beautyWen:home:JSONS"),
    SYS_BEAUTYWEN_HOME_IMG_URL(2,"system:beautyWen:home:img:url"),
    SYS_BEAUTYWEN_HOME_IMG_URL_INDEX(2,"system:beautyWen:home:img:url:index"),
    SYS_ERROR_BEAUTYWEN_JSONS(2,"system:error:beautyWen:JSONS"),
    USER_SPITTINGGROOVES_JSON(1,"user:spittinggrooves:json"),
    USER_OFFICIAL_MSG(1,"user:official:msg"),
    SYS_PMW_INDEX(2,"user:spittinggrooves:count"),

    USER_DATA_ANALYSIS_MODEL(1,"user:data_analysis_model");

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