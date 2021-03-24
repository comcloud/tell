package com.yundingxi.tell.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (Reply)实体类
 *
 * @author makejava
 * @since 2021-03-24 08:34:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reply implements Serializable {
    private static final long serialVersionUID = -61199323300631797L;
    /**
     * 回信表主键
     */
    private String id;
    /**
     * 回复信的ID
     */
    private String letterId;
    /**
     * 回复时间
     */
    private Date replyTime;
    /**
     * 回复内容
     */
    private String content;
    /**
     * 回复者的open_id
     */
    private String openId;
    /**
     * 回复者的笔名
     */
    private String penName;

}
