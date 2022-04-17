package com.yundingxi.model.dto.letter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName UnreadMessageDto
 * @Author rayss
 * @Datetime 2021/3/28 2:16 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "未读消息",description = "未读消息实体类")
public class UnreadMessageDto {
    @ApiModelProperty(value = "发送者的open id",required = true)
    private String sender;
    @ApiModelProperty(value = "接收者的open id",required = true)
    private String recipient;
    @ApiModelProperty(value = "未读消息",required = true)
    private String message;
    @ApiModelProperty(value = "发送这个消息的时间",required = true)
    private String senderTime;
    @ApiModelProperty(value = "回复人留下的笔名",required = true)
    private String senderPenName;
    @ApiModelProperty(value = "接收者的笔名",required = true)
    private String recipientPenName;
    @ApiModelProperty(value = "被回复信的ID",required = true)
    private String letterId;
    @ApiModelProperty(value = "回复信的id",required = true)
    private String replyId;
}
