package com.yundingxi.tell.util.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName SubMessageTemplateEnum
 * @Author rayss
 * @Datetime 2021/5/29 9:35 上午
 */

public enum SubMessageTemplateEnum {
    /**
     * 订阅消息评论模版ID
     */
    SUB_MESSAGE_COMMENT_TEMPLATE_ID("mghtoN9x1YBMmyWg9RtBlt8-XxHxMvEo8eAtHIazD34","com.yundingxi.tell.util.strategy.SubMessageStrategyContext$CommentSubMessageStrategy"),
    /**
     * 订阅消息回信ID
     */
    SUB_MESSAGE_REPLY_LETTER_TEMPLATE_ID("vuxCjKVvzbUWW1iHbMkSCmsBrpXWkXFPJ81S8nVWJdw","com.yundingxi.tell.util.strategy.SubMessageStrategyContext$ReplySubMessageStrategy");

    private final String templateId;
    private final String className;

    SubMessageTemplateEnum(String templateId, String className) {
        this.templateId = templateId;
        this.className = className;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getClassName() {
        return className;
    }
    public static Map<String,String> getAllClazz(){
        Map<String, String> map = new HashMap<>(4);
        for (SubMessageTemplateEnum value : SubMessageTemplateEnum.values()) {
            map.put(value.getTemplateId(),value.getClassName());
        }
        return map;
    }
}
