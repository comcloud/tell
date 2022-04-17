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
 * @ClassName LetterVo
 * @Author rayss
 * @Datetime 2021/5/6 2:05 下午
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("历史信件实体类")
public class LetterVo {
    @ApiModelProperty(value = "信件的内容",required = true)
    private String content;
    @ApiModelProperty(value = "发信者open id",required = true)
    private String openId;
    @ApiModelProperty(value = "信件id",required = true)
    private String id;
    @ApiModelProperty(value = "发布信件的笔名",required = true)
    private String penName;
    @ApiModelProperty(value = "邮票地址")
    private String stampUrl;
    @ApiModelProperty(value = "发布时间",required = true)
    private Date releaseTime;
}