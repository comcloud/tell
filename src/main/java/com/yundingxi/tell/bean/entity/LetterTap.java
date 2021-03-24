package com.yundingxi.tell.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (LetterTap)实体类
 *
 * @author makejava
 * @since 2021-03-24 08:34:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterTap implements Serializable {
    private static final long serialVersionUID = 314974066085607197L;
    /**
     * 信的标签主键
     */
    private String id;
    /**
     * 标签名
     */
    private String name;
    /**
     * 标签使用数量
     */
    private Long count;


}
