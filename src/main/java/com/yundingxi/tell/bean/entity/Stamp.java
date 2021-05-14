package com.yundingxi.tell.bean.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 * 
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Stamp对象", description="")
public class Stamp{

    @ApiModelProperty(value = "主键")
    private String id= UUID.randomUUID().toString();

    @ApiModelProperty(value = "邮票图片地址")
    private String stampUrl;

    @ApiModelProperty(value = "邮票标题")
    private String stampName;

    @ApiModelProperty(value = "邮票编号")
    private String stampNumber;

    @ApiModelProperty(value = "邮票描述")
    private String stampDesc;

    @ApiModelProperty(value = "邮票版本")
    private String stampEdition;
    @ApiModelProperty(value = "邮票系列")
    private String stampSeries;

    @ApiModelProperty(value = "状态")
    private String state;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime stampCreateTime;



}
