package com.yundingxi.tell.bean.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName LetterStorageDto
 * @Author rayss
 * @Datetime 2021/4/16 8:27 下午
 */

@ApiModel("保存信件实体类")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LetterStorageDto {
    @ApiModelProperty(value = "信件内容",required = true)
    private String content;
    @ApiModelProperty(value = "发布者用户open id",required = true)
    private String openId;
    @ApiModelProperty(value = "笔名",required = true)
    private String penName;
    @ApiModelProperty(value = "邮票地址",required = true)
    private String stampUrl;
    @ApiModelProperty(value = "状态 ： 1正常 2 未通过 3 待审核 4 删除",required = true)
    private Integer state;
    @ApiModelProperty(value = "标签id集合,以,分隔",required = true)
    private String tapIds;
}
