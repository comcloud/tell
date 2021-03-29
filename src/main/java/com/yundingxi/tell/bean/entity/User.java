<<<<<<< HEAD
package com.yundingxi.tell.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;
import java.util.UUID;

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
public class User implements Serializable {
    private static final long serialVersionUID = 902961327540672606L;
    /**
     * 用户组件
     */
    private String openId;
    /**
     * 出生日期
     */
    private Date dateBirth=new Date();
    /**
     * 0：女 1：男
     */
    private Integer gender;
    /**
     * 注册时间
     */
    private Date registrationTime=new Date();
    /**
     * 最后一次登陆时间
     */
    private Date lastLoginTime =new Date();
    /**
     * 状态 0  正常，1 停用
     */
    private Integer state =0;
    /**
     * 笔名
     */
    private String penName;
    /**
     * 头像url
     */
    private String avatarUrl;

}
=======
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
>>>>>>> bfdd24e1169c7ec6f002af6ffa3a5e9485617afe
