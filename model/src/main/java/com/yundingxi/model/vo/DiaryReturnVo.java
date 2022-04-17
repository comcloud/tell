package com.yundingxi.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @version v1.0
 * @ClassName DiaryReturnDto
 * @Author rayss
 * @Datetime 2021/5/6 11:12 上午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "日记返回实体")
public class DiaryReturnVo {
    @ApiModelProperty(value = "日记id",required = true)
    private String id;
    @ApiModelProperty(value = "日记内容",required = true)
    private String content;
    @ApiModelProperty(value = "笔名",required = true)
    private String penName;
    @ApiModelProperty(value = "天气",required = true)
    private String weather;
    @ApiModelProperty(value = "open id",required = true)
    private String openId;
    @ApiModelProperty(value = "state，1表示不公开，0表示公开",required = true)
    private String state;
    @ApiModelProperty(value = "日记浏览量",required = true)
    private String number;
    @ApiModelProperty(value = "信件发布时间",required = true)
    private Date date;


}
