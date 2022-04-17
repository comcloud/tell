package com.yundingxi.common.model.enums;

import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * API枚举类
 * @version v1.0
 * @ClassName ApiEnums
 * @Author rayss
 * @Datetime 2021/4/24 11:24 上午
 */

public enum ApiEnums {

    /**
     * 情感倾向分析
     */
    EMOTIONAL_TENDENCY("https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify", RequestMethod.POST);

    /**
     * 请求地址
     */
    @Getter
    private final String requestUrl;
    /**
     * 请求方法
     */
    @Getter
    private final RequestMethod requestMethod;

    ApiEnums(String requestUrl, RequestMethod requestMethod) {
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
    }
}
