package com.yundingxi.tell.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @version v1.0
 * @ClassName IndexLetterDto
 * @Author rayss
 * @Datetime 2021/4/13 12:42 下午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("首页信件实体类")
public class IndexLetterDto {
    @ApiModelProperty(value = "信件的内容",required = true)
    private String content;
    @ApiModelProperty(value = "发信者open id",required = true)
    private String senderOpenId;
    @ApiModelProperty(value = "信件id",required = true)
    private String id;
    @ApiModelProperty(value = "发布信件的笔名",required = true)
    private String senderPenName;
    @ApiModelProperty(value = "邮票地址")
    private String stampUrl;
    @ApiModelProperty(value = "接收者的笔名",required = true)
    private String recipientPenName;
    @ApiModelProperty(value = "发布时间",required = true)
    private Date releaseTime;
}
