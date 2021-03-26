package com.yundingxi.tell.common;

import com.yundingxi.tell.bean.vo.LetterVo;
import com.yundingxi.tell.util.message.SendMailUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @version v1.0
 * @ClassName ScheduledSyncService
 * @Author rayss
 * @Datetime 2021/3/26 4:04 下午
 */

@Component
public class ScheduledSyncService {

    @Scheduled(cron = "0/10 * * * * ?")
    public void executeReplyLetterFromQueue(){
        BlockingDeque<LetterVo> waitQueue = SendMailUtil.getWAIT_QUEUE();
        if(waitQueue.isEmpty()) {
            return;
        }
        ThreadPoolExecutor pool = SendMailUtil.getPOOL();
        waitQueue.forEach(letterVo -> pool.execute(new SendMailUtil.LetterTask(letterVo)));
    }
}
