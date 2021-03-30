package com.yundingxi.tell.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value="SpittingGrooves对象", description="")
public class SpittingGrooves implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "吐槽id")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private String id;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "发布日期")
    private Date date;

    @ApiModelProperty(value = "吐槽评论数量")
    private String number;

    @ApiModelProperty(value = "0：正常 1 删除")
    private String state;

    @ApiModelProperty(value = "发布人openid")
    private String openId;


}
