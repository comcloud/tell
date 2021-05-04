package com.yundingxi.tell.common.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @version v1.0
 * @ClassName DynamicDatasource
 * @Author rayss
 * @Datetime 2021/5/4 2:28 下午
 */

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDataSource();
    }
}
