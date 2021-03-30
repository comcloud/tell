package com.yundingxi.tell.bean.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Diarys对象", description="日记")
public class Diarys implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "浏览量")
    private String number;

    @ApiModelProperty(value = "笔名")
    private String penName;

    @ApiModelProperty(value = "天气")
    private String weather;

    @ApiModelProperty(value = "状态：0 ：公开 ：1私有 2删除")
    private String state;

    @ApiModelProperty(value = "发布用户的openid")
    private String openId;


}
