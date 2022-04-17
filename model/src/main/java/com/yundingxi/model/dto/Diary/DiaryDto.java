package com.yundingxi.model.dto.Diary;

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
    @ApiModelProperty(value = "日记内容",required = true)
    private String content;
    @ApiModelProperty(value = "笔名",required = true)
    private String penName;
    @ApiModelProperty(value = "天气",required = true)
    private String weather;
    @ApiModelProperty(value = "open id",required = true)
    private String openId;
    @ApiModelProperty(value = "状态 ： 1正常 2 未通过 3 待审核 4 删除",required = true)
    private String state;
}
