package com.yundingxi.tell.common.listener;

import com.yundingxi.tell.common.CenterThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ThreadPoolExecutor;

/**
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


    /**
     * 处理保存信件事件
     * @param letterEvent 信件事件
     */
    @EventListener
    public void handleSaveLetterEvent(PublishLetterEvent letterEvent){
        LOG.info("触发保存信件事件，此时应该更新关于信件的成就内容");
        LOG.info(letterEvent.getLetterStorageDto().toString());
        EXECUTOR.execute(() -> {

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
