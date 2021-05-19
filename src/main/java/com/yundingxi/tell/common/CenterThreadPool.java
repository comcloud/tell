package com.yundingxi.tell.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yundingxi.tell.util.message.ResizableCapacityLinkedBlockingQueue;
import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 中心线程池，应用程序中开辟的每一个线程始终从此处获取新的执行线程
 *
 * @version v1.0
 * @ClassName CenterThreadPool
 * @Author rayss
 * @Datetime 2021/5/12 4:13 下午
 */

public class CenterThreadPool {
    /*** 核心线程数*/
    private static final int CORE_POOL_SIZE = 4;

    /*** 最大线程数*/
    private static final int MAX_POOL_SIZE = 10;

    /*** 当线程数大于核心时，这是多余空闲线程在终止前等待新任务的最长时间。*/
    private static final int KEEP_ALIVE_TIME = 0;

    /**
     * websocket线程池
     */
    @Getter
    private static final ThreadPoolExecutor WEBSOCKET_POOL;

    /**
     * 邮票成就线程池
     */
    @Getter
    private static final ThreadPoolExecutor STAMP_ACHIEVE_POOL;

    /**
     * 业务处理线程池
     */
    @Getter
    private static final ThreadPoolExecutor BUSINESS_POOL;

    static {
        // 自定义线程工厂
        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        WEBSOCKET_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE
                , MAX_POOL_SIZE
                , KEEP_ALIVE_TIME
                , TimeUnit.SECONDS
                , new ResizableCapacityLinkedBlockingQueue<>(100)
                , threadFactoryBuilder.setNameFormat("websocket-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy());
        STAMP_ACHIEVE_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE
                , MAX_POOL_SIZE
                , KEEP_ALIVE_TIME
                , TimeUnit.SECONDS
                , new ResizableCapacityLinkedBlockingQueue<>(100)
                , threadFactoryBuilder.setNameFormat("stamp_achieve-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy());

        BUSINESS_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE
                , MAX_POOL_SIZE
                , KEEP_ALIVE_TIME
                , TimeUnit.SECONDS
                , new ResizableCapacityLinkedBlockingQueue<>(100)
                , threadFactoryBuilder.setNameFormat("business-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy());

    }
}
