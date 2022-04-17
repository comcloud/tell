package com.yundingxi.dao.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
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
@ApiModel(value="Task对象", description="")
public class Task{

    @ApiModelProperty(value = "任务主键")
    private String id= UUID.randomUUID().toString();

    @ApiModelProperty(value = "任务名")
    private String name;

    @ApiModelProperty(value = "任务sql")
    private String taskJosn;

    @ApiModelProperty(value = "任务描述")
    private String taskDescription;

    @ApiModelProperty(value = "任务状态")
    private String state;
    @ApiModelProperty(value = "创建时间")
    private Date taskCreateTime;
    @ApiModelProperty(value = "任务奖励")
    private String taskReward;

}
