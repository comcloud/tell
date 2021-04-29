package com.yundingxi.tell.bean.entity;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SpittingGrooves对象", description="吐槽")
public class SpittingGrooves implements Serializable {
    private final Integer INDEX=20;
    private final Integer END=30;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "吐槽id")
    private String id= UUID.randomUUID().toString();

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
    @ApiModelProperty(value = "吐槽标题")
    private String title;
    @ApiModelProperty(value = "吐槽发布人头像")
    private String avatarUrl;
    @ApiModelProperty(value = "发布者笔名")
    private String penName;


    public void subStringTitle(){
        this.date=new Date();
        int i = this.content.indexOf("。", INDEX);
        if (i>INDEX&&i<=END){
            this.title= content.substring(0,i);
        }else {
            this.title=content.substring(0, 25);
        }
    }

}
