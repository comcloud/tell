package com.yundingxi.tell.bean.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value="Achieve对象", description="")
public class Achieve {


    @ApiModelProperty(value = "成就奖励")
    private String id;

    @ApiModelProperty(value = "成就图片地址")
    private String achieveUrl;

    @ApiModelProperty(value = "成就描述")
    private String achieveDesc;

    @ApiModelProperty(value = "对应任务")
    private String taskId;

    @ApiModelProperty(value = "创建时间")
    private String achieveCreateTime;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "成就版本")
    private String achieveEdition;

    @ApiModelProperty(value = "成就奖励")
    private String achieveReward;




}
