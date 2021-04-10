package com.yundingxi.tell.bean.vo;

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
 * @date 2021/4/9-14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommentVo {
    @ApiModelProperty(value = "吐槽id")
    private String sgId;
    @ApiModelProperty(value = "评论内容")
    private String content;
    @ApiModelProperty(value = "评论时间")
    private Date date;
    @ApiModelProperty(value = "评论那个吐槽内容")
    private String title;
    @ApiModelProperty(value = "评论USerVo")
    private UserVo userVo;
}
