package com.yundingxi.dao.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel("信件对象")
public class Letter implements Serializable {
    private static final long serialVersionUID = -29661735214638005L;
    /**
     * Id主键
     */
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 邮票图片路径
     */
    @ApiModelProperty(value = "邮票图片路径")
    private String stampUrl;
    /**
     * 发布用户openid
     */
    @ApiModelProperty(value = "发布用户openid")
    private String openId;
    /**
     * 详细内容
     */
    @ApiModelProperty(value = "详细内容")
    private String content;
    /**
     * 状态 ： 0 暂存 1发布 2 删除
     */
    @ApiModelProperty(value = "状态 ： 0 暂存 1发布 2 删除")
    private Integer state;
    /**
     * 笔名
     */
    @ApiModelProperty(value = "笔名")
    private String penName;
    /**
     * 标签id集合
     */
    @ApiModelProperty(value = "标签id集合")
    private String tapIds;
    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

}
