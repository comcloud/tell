package com.yundingxi.tell.common;

import com.yundingxi.tell.bean.vo.LetterWebsocketVo;
import com.yundingxi.tell.util.message.SendMailUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version v1.0
 * @ClassName ScheduledSyncService
 * @Author rayss
 * @Datetime 2021/3/26 4:04 下午
 */

@Component
public class ScheduledSyncService {

    /**
     * 暂时不使用websocket发送信件，而是客户端轮询主动拉取
     * 定时发送未发送的信件
     */
    @Deprecated
//    @Scheduled(cron = "0/100 * * * * ?")
    public void executeReplyLetterFromQueue(){
        BlockingDeque<LetterWebsocketVo> waitQueue = SendMailUtil.getWAIT_QUEUE();
        if(waitQueue.isEmpty()) {
            return;
        }
        ThreadPoolExecutor pool = SendMailUtil.getPOOL();
        ReentrantLock mainLock = SendMailUtil.getLOCK();
        mainLock.lock();
        try {
            for (int i = 0; i < waitQueue.size(); i++) {
                LetterWebsocketVo letterWebsocketVo = waitQueue.peek();
                pool.execute(new SendMailUtil.LetterTask(letterWebsocketVo));
            }
        }finally {
            mainLock.unlock();
        }
    }
}
