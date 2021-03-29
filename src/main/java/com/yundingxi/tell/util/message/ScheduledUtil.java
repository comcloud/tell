package com.yundingxi.tell.util.message;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @version v1.0
 * @ClassName ScheduledUtil
 * @Author rayss
 * @Datetime 2021/3/29 3:18 下午
 */

public class ScheduledUtil {
    /**
     * @param task 执行任务
     * @param delay 延迟时间，时间单位为小时
     */
    public static void delayNewTask(Runnable task, int delay){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, TimeUnit.HOURS.toMillis(delay));
    }
}
