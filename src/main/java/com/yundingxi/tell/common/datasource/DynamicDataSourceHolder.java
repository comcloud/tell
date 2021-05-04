package com.yundingxi.tell.common.datasource;

/**
 * @version v1.0
 * @ClassName DynamicDataSourceHolder
 * @Author rayss
 * @Datetime 2021/5/4 2:29 下午
 */

public class DynamicDataSourceHolder {
    private static final ThreadLocal<DatabaseType> THREAD_DATA_SOURCE = new ThreadLocal<>();

    public static DatabaseType getDataSource(){
        return THREAD_DATA_SOURCE.get();
    }

    public static void setDataSource(DatabaseType type){
        THREAD_DATA_SOURCE.set(type);
    }
    public static void clearDataSource(){
        THREAD_DATA_SOURCE.remove();
    }
}
