package com.yundingxi.tell.common.listener;

import com.yundingxi.tell.bean.entity.SpittingGrooves;
import org.springframework.context.ApplicationEvent;

/**
 * @version v1.0
 * @ClassName PublishSpitEvent
 * @Author rayss
 * @Datetime 2021/5/12 5:24 下午
 */

public class PublishSpitEvent extends ApplicationEvent {

    private SpittingGrooves spittingGrooves;

    public PublishSpitEvent(Object source, SpittingGrooves spittingGrooves) {
        super(source);
        this.spittingGrooves = spittingGrooves;
    }

    public SpittingGrooves getSpittingGrooves() {
        return spittingGrooves;
    }
}
