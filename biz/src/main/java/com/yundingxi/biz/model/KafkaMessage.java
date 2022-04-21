package com.yundingxi.biz.model;

import lombok.Builder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @version v1.0
 * @ClassName KafkaMessage
 * @Author rayss
 * @Datetime 2022/4/17 9:40 下午
 */
@Builder
@Component
public class KafkaMessage<T> implements Serializable, FactoryBean<T> {

    private static final long serialVersionUID = 1L;

    private final T object;

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return object.getClass();
    }

}
