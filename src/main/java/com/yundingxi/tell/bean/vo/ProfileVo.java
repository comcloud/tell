package com.yundingxi.tell.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
@ApiModel("个人用户内容实体类")
public class ProfileVo {
    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("头像")
    private String avatarUrl;
    @ApiModelProperty("内容数量")
    private List<ProfileNumVo> models;
    @ApiModelProperty("回信数量")
    private Integer backLetter;
}
