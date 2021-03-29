package com.yundingxi.tell.bean.dto;

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
    @ApiModelProperty("发送者的open id")
    private String sender;
    @ApiModelProperty("接收者的open id")
    private String recipient;
    @ApiModelProperty("未读消息")
    private String message;
    @ApiModelProperty("发送这个消息的时间")
    private String senderTime;
}
