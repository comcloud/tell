package com.yundingxi.model.vo.submessage;

import com.yundingxi.common.model.enums.WeChatEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName SubMessageParam
 * @Author rayss
 * @Datetime 2021/5/22 3:57 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubMessageParam {
    /**
     * 来源ID，比如被评论的吐槽，被回复的信件
     */
    private String parentId;
    /**
     * 展示内容，表示着要用来回复的内容
     */
    private String showContent;
    /**
     * 被回复的标题，是被回复内容的前一部分
     */
    private String title;
    /**
     * 回复者的昵称
     */
    private String nickname;
    /**
     * 接受者open id
     */
    private String touser;
    /**
     * 发送者open id
     */
    private String sender;
    /**
     * 补充对象，可以用来存放其他内容
     */
    private Object obj;
    /**
     * 模版ID
     */
    private WeChatEnum templateId;
    /**
     * 也跳转的页面路由
     */
    private WeChatEnum page;
    /**
     * 小程序版本
     */
    private WeChatEnum version;
}
