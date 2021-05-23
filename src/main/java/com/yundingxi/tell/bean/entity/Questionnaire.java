package com.yundingxi.tell.bean.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (Questionnaire)实体类
 *
 * @author makejava
 * @since 2021-05-23 14:48:51
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="调查问卷对象", description="")
@AllArgsConstructor
@NoArgsConstructor
public class Questionnaire implements Serializable {
    private static final long serialVersionUID = -47979226932002462L;
    /**
     * 个人用户open id
     */
    @ApiModelProperty(value = "open id",required = true)
    private String openId;
    /**
     * 是否存在不合法内容，1.从未出现,2.偶尔出现,3.经常出现
     */
    @ApiModelProperty(value = "是否存在不合法内容，1.从未出现,2.偶尔出现,3.经常出现",required = true)
    private Integer isIllegal;
    /**
     * 是否有帮助，1.非常有帮助，2.有一定帮助，3.没有帮助
     */
    @ApiModelProperty(value = "是否有帮助，1.非常有帮助，2.有一定帮助，3.没有帮助",required = true)
    private Integer isHelp;
    /**
     * 小程序是否有趣，分数级别：1.2.3.4.5
     */
    @ApiModelProperty(value = "小程序是否有趣，分数级别：1.2.3.4.5",required = true)
    private Integer interestScore;
    /**
     * 页面打分，分级：1.2.3.4.5
     */
    @ApiModelProperty(value = "页面打分，分级：1.2.3.4.5",required = true)
    private Integer pageScore;
    /**
     * 补充言语，表示着想对我们说
     */
    @ApiModelProperty(value = "补充话语，想对我们说",required = true)
    private String otherSpeech;
    /**
     * 填写调查问卷时间
     */
    @ApiModelProperty("填写调查问卷时间,不用填写")
    private Date date;
}
