package com.yundingxi.tell.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName DiaryDto
 * @Author rayss
 * @Datetime 2021/3/30 10:57 上午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "日记实体")
public class DiaryDto {
    @ApiModelProperty("日记内容")
    private String content;
    @ApiModelProperty("笔名")
    private String penName;
    @ApiModelProperty("天气")
    private String weather;
    @ApiModelProperty("open id")
    private String openId;
    @ApiModelProperty("state，0表示不公开，1表示公开")
    private String state;
}
