package com.yundingxi.tell.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (UserTap)实体类
 *
 * @author makejava
 * @since 2021-03-24 08:34:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTap implements Serializable {
    private static final long serialVersionUID = -89166060372252197L;
    /**
     * user标签
     */
    private String id;
    /**
     * 标签名
     */
    private String name;
}
