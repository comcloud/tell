package com.yundingxi.dao.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value="Reply对象", description="")
@AllArgsConstructor
@NoArgsConstructor
public class Reply implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "回信表主键")
    private String id;

    @ApiModelProperty(value = "回复信的ID")
    private String letterId;

    @ApiModelProperty(value = "回复时间")
    private Date replyTime;

    @ApiModelProperty(value = "回复内容")
    private String content;

    @ApiModelProperty(value = "回复者的open_id")
    private String openId;

    @ApiModelProperty(value = "回复者的笔名")
    private String penName;


}
