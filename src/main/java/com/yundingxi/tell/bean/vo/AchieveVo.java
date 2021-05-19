package com.yundingxi.tell.bean.vo;

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
 * @date 2021/5/14-11:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchieveVo {
    @ApiModelProperty(value = "成就图片地址")
    private String achieveUrl;
    @ApiModelProperty(value = "成就描述")
    private String achieveDesc;
    @ApiModelProperty(value = "成就版本")
    private String achieveEdition;
    @ApiModelProperty(value = "成就名")
    private String achieveName;
    @ApiModelProperty(value = "成就名")
    private Date obtainTime;
    @ApiModelProperty(value = "是否解锁")
    private boolean isLock;
}
