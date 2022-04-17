package com.yundingxi.model.dto.letter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName ReplyInfoDto
 * @Author rayss
 * @Datetime 2021/4/13 8:26 上午
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("获取回复信件信息")
public class ReplyInfoDto {
    @ApiModelProperty(value = "open id",required = true)
    private String openId;
    @ApiModelProperty(value = "letter信件id")
    private String letterId;
    @ApiModelProperty(value = "reply信件id",required = true)
    private String replyId;
}
