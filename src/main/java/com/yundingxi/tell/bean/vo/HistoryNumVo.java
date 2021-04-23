package com.yundingxi.tell.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName HistoryNumVo
 * @Author rayss
 * @Datetime 2021/4/22 5:06 下午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("历史数量实体类")
public class HistoryNumVo {
    @ApiModelProperty("类别名")
    private String name;
    @ApiModelProperty("数量")
    private int value;
}
