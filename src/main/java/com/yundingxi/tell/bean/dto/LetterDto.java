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
    @ApiModelProperty("被回复信件的内容一部分")
    private String lastContent;
    @ApiModelProperty("回复的信件内容")
    private String replyContent;
    @ApiModelProperty("信件id")
    private String id;
    @ApiModelProperty("发布信件的笔名")
    private String senderPenName;
    @ApiModelProperty("接收者的笔名")
    private String recipientPenName;
    @ApiModelProperty("发布时间")
    private Date releaseTime;
}
