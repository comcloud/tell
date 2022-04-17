package com.yundingxi.biz.infrastructure.strategy;


import com.yundingxi.model.vo.submessage.SubMessageParam;

/**
 * 订阅消息策略接口
 * @version v1.0
 * @ClassName SubMessageStrategy
 * @Author rayss
 * @Datetime 2021/5/29 8:52 上午
 */

public interface SubMessageStrategy {
    /**
     * 处理订阅消息，将其
     * @param param 参数
     * @param reserveParam 预留参数
     */
    void processSubMessage(SubMessageParam param, String... reserveParam);
}
