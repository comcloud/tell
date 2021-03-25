package com.yundingxi.tell.common.enums;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/25-9:14
 */
public interface RedsiBaseEnums {
    /**
     * 获取几号数据库
     * @return
     */
    int getRedisDbIndex();
    /**
     * 获取Key
     */
    String getRedisKey();
}
