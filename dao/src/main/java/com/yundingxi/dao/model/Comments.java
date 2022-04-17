package com.yundingxi.dao.model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * 
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value="Comments对象", description="评论")
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "吐槽评论表ID")
    private String id = UUID.randomUUID().toString();

    @ApiModelProperty(value = "评论人openid")
    private String openId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "评论状态  0 ： 正常 1删除")
    private String state;

    @ApiModelProperty(value = "评论时间")
    private Date date;
    @ApiModelProperty(value = "定论的对象的ID")
    private String sgId;


}
