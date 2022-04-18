package com.yundingxi.biz.model;

import lombok.Builder;

import java.io.Serializable;

/**
 * @version v1.0
 * @ClassName AchieveStampMessage
 * @Author rayss
 * @Datetime 2022/4/17 9:40 下午
 */
@Builder
public class AchieveStampMessage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final T object;

    public Class<?> getObjectType() {
        return object.getClass();
    }

}
