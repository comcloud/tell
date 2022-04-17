package com.yundingxi.web.configuration.datasource;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yundingxi.web.util.message.ResizableCapacityLinkedBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
@Configuration
public class CenterThreadPool {
    //--------------默认配置-----------
    /*** 核心线程数*/
    private static final int CORE_POOL_SIZE = 4;

    /*** 最大线程数*/
    private static final int MAX_POOL_SIZE = 10;

    /*** 当线程数大于核心时，这是多余空闲线程在终止前等待新任务的最长时间。*/
    private static final int KEEP_ALIVE_TIME = 0;

    private final ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();


    //---------------线程池配置------------

    /**
     * websocket线程池
     */

    private final BlockingQueue<Runnable> websocketWorkQueue = new ResizableCapacityLinkedBlockingQueue<>(100);

    @Bean
    public ThreadPoolExecutor websocketPool() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE
                , MAX_POOL_SIZE
                , KEEP_ALIVE_TIME
                , TimeUnit.SECONDS
                , websocketWorkQueue
                , threadFactoryBuilder.setNameFormat("websocket-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 邮票成就线程池
     */

    private final BlockingQueue<Runnable> stampAchieveWorkQueue = new ResizableCapacityLinkedBlockingQueue<>(100);

    @Bean
    public ThreadPoolExecutor stampAchievePool() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE
                , MAX_POOL_SIZE
                , KEEP_ALIVE_TIME
                , TimeUnit.SECONDS
                , stampAchieveWorkQueue
                , threadFactoryBuilder.setNameFormat("stamp_achieve-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy());
    }


    /**
     * 业务处理线程池
     */


    private final BlockingQueue<Runnable> businessWorkQueue = new ResizableCapacityLinkedBlockingQueue<>(100);

    @Bean
    public ThreadPoolExecutor businessPool() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE
                , MAX_POOL_SIZE
                , KEEP_ALIVE_TIME
                , TimeUnit.SECONDS
                , businessWorkQueue
                , threadFactoryBuilder.setNameFormat("business-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
