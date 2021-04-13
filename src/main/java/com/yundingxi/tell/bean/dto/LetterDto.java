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
 * @ClassName LetterDto
 * @Author rayss
 * @Datetime 2021/3/31 9:37 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("信件返回数据对象")
public class LetterDto {
    @ApiModelProperty(value = "被回复信件的内容一部分",required = true)
    private String lastContent;
    @ApiModelProperty(value = "回复的信件内容",required = true)
    private String replyContent;
    @ApiModelProperty(value = "信件id",required = true)
    private String id;
    @ApiModelProperty(value = "发布信件的笔名",required = true)
    private String senderPenName;
    @ApiModelProperty(value = "接收者的笔名",required = true)
    private String recipientPenName;
    @ApiModelProperty(value = "发布时间",required = true)
    private Date releaseTime;
}
