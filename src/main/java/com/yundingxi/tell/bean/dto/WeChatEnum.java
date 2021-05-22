package com.yundingxi.tell.bean.dto;

/**
 * @version v1.0
 * @ClassName WeChatEnum
 * @Author rayss
 * @Datetime 2021/5/17 4:09 下午
 */

public enum WeChatEnum {
    /**
     * 获取订阅消息的access_token请求
     */
    SUB_MESSAGE_ACCESS_TOKEN_URL_GET("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"),
    /**
     * 发起订阅消息的URL路径
     */
    SUB_MESSAGE_SEND_URL_POST("https://api.weixin.qq.com/cgi-bin/message/subscribe/send"),
    /**
     * 订阅消息评论模版ID
     */
    SUB_MESSAGE_COMMENT_TEMPLATE_ID("mghtoN9x1YBMmyWg9RtBlt8-XxHxMvEo8eAtHIazD34"),
    /**
     * 订阅消息回信ID
     */
    SUB_MESSAGE_REPLY_LETTER_TEMPLATE_ID("vuxCjKVvzbUWW1iHbMkSCmsBrpXWkXFPJ81S8nVWJdw"),
    /**
     * 订阅消息评论跳转page
     */
    SUB_MESSAGE_COMMENT_PAGE("packageWriteLetter/pages/complaintletter/complaintletter"),
    /**
     * 订阅消息回复跳转page
     */
    SUB_MESSAGE_REPLY_PAGE("packageMyInfo/pages/replyletter/replyletter"),
    /**
     * 订阅消息体验版
     */
    SUB_MESSAGE_MINI_PROGRAM_STATE_TRIAL_VERSION("trial"),
    /**
     * 订阅消息开发版
     */
    SUB_MESSAGE_MINI_PROGRAM_STATE_DEVELOPER_VERSION("developer"),
    /**
     * 订阅消息正式版
     */
    SUB_MESSAGE_MINI_PROGRAM_STATE_FORMAL_VERSION("formal");

    private final String value;

    WeChatEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
