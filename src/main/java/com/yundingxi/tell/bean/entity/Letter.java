package com.yundingxi.tell.bean.entity;
import	java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * (Letter)实体类
 *
 * @author makejava
 * @since 2021-03-24 08:34:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Letter implements Serializable {
    private static final long serialVersionUID = -29661735214638005L;
    /**
     * Id主键
     */
    private String id;
    /**
     * 邮票图片路径
     */
    private String stampUrl;
    /**
     * 发布用户openid
     */
    private String openId;
    /**
     * 详细内容
     */
    private String content;
    /**
     * 状态 ： 0 暂存 1发布 2 删除
     */
    private Integer state;
    /**
     * 笔名
     */
    private String penName;
    /**
     * 标签id集合
     */
    private String tapIds;
    /**
     * 发布时间
     */
    private Date releaseTime;

}
