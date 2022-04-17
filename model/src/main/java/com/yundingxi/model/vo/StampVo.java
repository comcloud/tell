package com.yundingxi.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/5/14-9:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StampVo {
    @ApiModelProperty(value = "邮票图片地址")
    private String stampUrl;

    @ApiModelProperty(value = "邮票标题")
    private String stampName;
    @ApiModelProperty(value = "邮票系列")
    private String stampSeries;
    @ApiModelProperty(value = "邮票编号")
    private String stampNumber;

    @ApiModelProperty(value = "邮票描述")
    private String stampDesc;

    @ApiModelProperty(value = "邮票版本")
    private String stampEdition;
    @ApiModelProperty(value = "获取时间")
    private Date obtainTime;

    @ApiModelProperty(value = "是否被锁")
    private boolean isLock;
}
