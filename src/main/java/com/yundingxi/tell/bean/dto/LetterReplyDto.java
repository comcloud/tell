package com.yundingxi.tell.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName LetterReplyDto
 * @Author rayss
 * @Datetime 2021/3/29 3:01 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "回复信件需要的信息")
public class LetterReplyDto {
    @ApiModelProperty(value = "要回复的人的open id",required = true)
    private String sender;
    @ApiModelProperty(value = "被回复的人的open id",required = true)
    private String recipient;
    @ApiModelProperty(value = "被回复的信件的id,后台需要是哪封信件被回复",required = true)
    private String letterId;
    @ApiModelProperty(value = "回复人留下的笔名",required = true)
    private String senderPenName;
    @ApiModelProperty(value = "接收者的笔名",required = true)
    private String recipientPenName;
    @ApiModelProperty(value = "回复人回复的消息",required = true)
    private String message;
}
