package com.yundingxi.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/30-17:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentVo {
    @ApiModelProperty(value = "评论内容")
    private String content;
    @ApiModelProperty(value = "评论时间")
    private Date date;
    @ApiModelProperty(value = "评论USerVo")
    private UserVo userVo;
    @ApiModelProperty(value = "评论数量")
    private Integer number;
}
