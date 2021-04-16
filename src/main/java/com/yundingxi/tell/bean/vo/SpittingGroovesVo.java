package com.yundingxi.tell.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SpittingGrooves对象", description="吐槽")
public class SpittingGroovesVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "吐槽id")
    private String id;
    @ApiModelProperty(value = "吐槽评论数量")
    private String number;
    @ApiModelProperty(value = "吐槽标题")
    private String title;
    @ApiModelProperty(value = "吐槽发布人头像")
    private String avatarUrl;
    @ApiModelProperty(value = "发布者笔名")
    private String penName;

}
