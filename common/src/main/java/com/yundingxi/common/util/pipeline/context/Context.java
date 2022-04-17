package com.yundingxi.common.util.pipeline.context;

import java.time.LocalDateTime;

/**
 * @version v1.0
 * @ClassName Context
 * @Author rayss
 * @Datetime 2021/7/21 1:55 下午
 */

public abstract class Context {

    /**
     * 处理开始时间
     */
    private LocalDateTime startTime;

    /**
     * 处理结束时间
     */
    private LocalDateTime endTime;

    /**
     * 获取数据名称
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
