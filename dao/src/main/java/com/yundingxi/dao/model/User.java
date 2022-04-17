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
 * (User)实体类
 *
 * @author makejava
 * @since 2021-03-24 08:34:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("User对象")
public class User implements Serializable {
    private static final long serialVersionUID = 902961327540672606L;
    /**
     * 用户组件
     */
    @ApiModelProperty(value = "主键")
    private String openId;
    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    private Date dateBirth=new Date();
    /**
     * 0：女 1：男
     */
    @ApiModelProperty(value = "0：女 1：男")
    private Integer gender;
    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间")
    private Date registrationTime;
    /**
     * 最后一次登陆时间
     */
    @ApiModelProperty(value = "最后一次登陆时间")
    private Date lastLoginTime =new Date();
    /**
     * 状态 0  正常，1 停用
     */
    @ApiModelProperty(value = "状态 0  正常，1 停用")
    private Integer state =0;
    /**
     * 笔名
     */
    @ApiModelProperty(value = "笔名")
    private String penName;
    /**
     * 头像url
     */
    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

}
