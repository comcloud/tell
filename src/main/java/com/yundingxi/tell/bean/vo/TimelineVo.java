package com.yundingxi.tell.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName TimelineVo
 * @Author rayss
 * @Datetime 2021/5/15 8:34 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("时间线内容实体类")
public class TimelineVo {
    @ApiModelProperty("事件发起者")
    private String senderName;
    @ApiModelProperty("事件类型，发日记，发吐槽，发解忧")
    private String eventType;
    @ApiModelProperty("事件时间")
    private String eventTime;
    @ApiModelProperty("发布的内容")
    private String content;
}
