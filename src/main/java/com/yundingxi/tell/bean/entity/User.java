package com.yundingxi.tell.bean.entity;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author houdongsheng
 * @since 2021-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户组件")
    private String openId;

    @ApiModelProperty(value = "出生日期")
    private Date dateBirth;

    @ApiModelProperty(value = "0：女 1：男")
    private Integer gender;

    @ApiModelProperty(value = "注册时间")
    private Date registrationTime;

    @ApiModelProperty(value = "最后一次登陆时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "状态 0  正常，1 停用")
    private Integer state;

    @ApiModelProperty(value = "笔名")
    private String penName;

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;


}
