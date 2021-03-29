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
@ApiModel(value="LetterTap对象", description="")
@NoArgsConstructor
@AllArgsConstructor
public class LetterTap implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "信的标签主键")
    private String id;

    @ApiModelProperty(value = "标签名")
    private String name;

    @ApiModelProperty(value = "标签使用数量")
    private Long count;


}
