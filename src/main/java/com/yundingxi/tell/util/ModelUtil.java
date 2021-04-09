package com.yundingxi.tell.util;

/**
 * @author 成都犀牛
 * @version version 1.0
 * @date 2020/10/6 18:30
 */
public class ModelUtil<T,R> {
    private T firstValue;
    private R lastValue;

    public T getFirstValue() {
        return firstValue;
    }

    public ModelUtil<T, R> setFirstValue(T firstValue) {
        this.firstValue = firstValue;
        return this;
    }

    public R getLastValue() {
        return lastValue;
    }

    public ModelUtil<T, R> setLastValue(R lastValue) {
        this.lastValue = lastValue;
        return this;
    }
}
