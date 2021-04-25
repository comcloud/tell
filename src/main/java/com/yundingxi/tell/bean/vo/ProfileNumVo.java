package com.yundingxi.tell.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.Pipe;

/**
 * @version v1.0
 * @ClassName ProfileNumVo
 * @Author rayss
 * @Datetime 2021/4/24 10:49 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("历史数量实体类")
public class ProfileNumVo {
    @ApiModelProperty("类型名")
    private String name;
    @ApiModelProperty("数量")
    private int value;
}
