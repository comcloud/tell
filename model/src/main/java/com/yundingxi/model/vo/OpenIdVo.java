package com.yundingxi.model.vo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version v1.0
 * @ClassName OpenIdVo
 * @Author rayss
 * @Datetime 2021/4/14 3:35 下午
 */

@Component
@ConfigurationProperties("wechat")
public class OpenIdVo {
    private String appid;
    private String secret;
    private String grantType;
    private String baseUrl;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
