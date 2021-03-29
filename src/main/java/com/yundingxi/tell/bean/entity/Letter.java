<<<<<<< HEAD
package com.yundingxi.tell.bean.entity;
import	java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * (Letter)实体类
 *
 * @author makejava
 * @since 2021-03-24 08:34:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Letter implements Serializable {
    private static final long serialVersionUID = -29661735214638005L;
    /**
     * Id主键
     */
    private String id;
    /**
     * 邮票图片路径
     */
    private String stampUrl;
    /**
     * 发布用户openid
     */
    private String openId;
    /**
     * 详细内容
     */
    private String content;
    /**
     * 状态 ： 0 暂存 1发布 2 删除
     */
    private Integer state;
    /**
     * 笔名
     */
    private String penName;
    /**
     * 标签id集合
     */
    private String tapIds;
    /**
     * 发布时间
     */
    private Date releaseTime;

}
=======
package com.yundingxi.tell.bean.entity;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author houdongsheng
 * @since 2021-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Letter对象", description="")
@NoArgsConstructor
@AllArgsConstructor
public class Letter implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Id主键")
    private String id;

    @ApiModelProperty(value = "邮票图片路径")
    private String stampUrl;

    @ApiModelProperty(value = "发布用户openid")
    private String openId;

    @ApiModelProperty(value = "详细内容")
    private String content;

    @ApiModelProperty(value = "状态 ： 0 暂存 1发布 2 删除")
    private Integer state;

    @ApiModelProperty(value = "笔名")
    private String penName;

    @ApiModelProperty(value = "标签id集合")
    private String tapIds;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;


}
>>>>>>> bfdd24e1169c7ec6f002af6ffa3a5e9485617afe
