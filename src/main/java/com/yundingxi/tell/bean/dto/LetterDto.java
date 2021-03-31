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
    @ApiModelProperty("信件内容")
    private String content;
    @ApiModelProperty("信件id")
    private String id;
    @ApiModelProperty("发布信件的笔名")
    private String penName;
    @ApiModelProperty("发布时间")
    private Date releaseTime;
    @ApiModelProperty("邮票图片地址")
    private String stampUrl;
}
