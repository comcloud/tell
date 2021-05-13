package com.yundingxi.tell.common.listener;

import com.yundingxi.tell.common.CenterThreadPool;
import com.yundingxi.tell.common.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 这里执行
 * @version v1.0
 * @ClassName CustomListenerConfig
 * @Author rayss
 * @Datetime 2021/5/12 5:01 下午
 */

@Configuration
public class CustomListenerConfig {

    private final Logger LOG = LoggerFactory.getLogger(CustomListenerConfig.class);

    /**
     * 线程池
     */
    private final ThreadPoolExecutor EXECUTOR = CenterThreadPool.getStampAchievePool();

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 处理保存信件事件
     * @param letterEvent 信件事件
     */
    @EventListener
    public void handleSaveLetterEvent(PublishLetterEvent letterEvent){
        LOG.info("触发保存信件事件，此时应该更新关于信件的成就内容");
        LOG.info(letterEvent.getLetterStorageDto().toString());
        EXECUTOR.execute(() -> {
            /*
              这时候要做的事情
              letter
              1.根据成就的类型achieve_type来获取对应成就已经获取到的位置
                - 位置每个人都默认是1（不一定在数据库第一位，只是每个类型的第一位），也就是从第一个开始
              2.根据获取到的成就位置查询对应的任务JSON
              3.读取JSON拼接为sql语句查询数据库判断是否已经完成此任务
               - 完成返回true，表示完成的话，需要将位置+1，同时给予对应的奖励achieve_reward，也就是对应的邮票
               - 未完成返回false，什么都不做
              */

        });
    }

    /**
     * 处理保存日记事件
     * @param diaryEvent 日记事件
     */
    @EventListener
    public void handleSaveDiary(PublishDiaryEvent diaryEvent){
        LOG.info("触发保存日记事件，此时应该更新关于日记的成就内容");
        LOG.info(diaryEvent.getDiaryDto().toString());
        EXECUTOR.execute(() -> {

        });
    }

    /**
     * 处理保存吐槽事件
     * @param spitEvent 吐槽事件
     */
    @EventListener
    public void handleSaveSpit(PublishSpitEvent spitEvent){
        LOG.info("触发保存吐槽事件，此时应该更新关于吐槽的成就内容");
        LOG.info(spitEvent.getSpittingGrooves().toString());
        EXECUTOR.execute(() -> {

        });
    }

}
