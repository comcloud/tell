package com.yundingxi.tell.bean.entity;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserTap对象", description="")
@AllArgsConstructor
@NoArgsConstructor
public class UserTap implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "user标签")
    private String id;

    @ApiModelProperty(value = "标签名")
    private String name;


}
