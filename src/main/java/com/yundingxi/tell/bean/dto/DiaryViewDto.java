package com.yundingxi.tell.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version v1.0
 * @ClassName DiaryViewDto
 * @Author rayss
 * @Datetime 2021/4/9 5:54 下午
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("日记浏览量实体类")
public class DiaryViewDto {
    @ApiModelProperty(value = "日记id",dataType = "java.lang.String")
    private String diaryId;
    @ApiModelProperty(value = "日记变化的浏览量",dataType = "java.lang.Integer")
    private Integer viewNum;

}
