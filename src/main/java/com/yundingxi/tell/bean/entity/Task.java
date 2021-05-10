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
@ApiModel(value="Task对象", description="")
public class Task{


    @ApiModelProperty(value = "任务主键")
    private String id;

    @ApiModelProperty(value = "任务名")
    private String name;

    @ApiModelProperty(value = "任务sql")
    private String taskSql;

    @ApiModelProperty(value = "任务描述")
    private String taskDescription;

    @ApiModelProperty(value = "任务状态")
    private String satet;



}
