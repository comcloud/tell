package com.yundingxi.biz.model;

import org.springframework.context.ApplicationEvent;

/**
 * @version v1.0
 * @ClassName UserBehaiorEvent
 * @Author rayss
 * @Datetime 2021/7/12 11:51 上午
 */

public class UserBehaviorEvent<T> extends ApplicationEvent {

    private final T t;
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserBehaviorEvent(Object source, T t) {
        super(source);
        this.t = t;
    }

    public T getT(){
        return this.t;
    }

}
