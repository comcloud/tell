package com.yundingxi.tell.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/30-17:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVo {
    /**
     * 笔名
     */
    private String penName;
    /**
     * 头像url
     */
    private String avatarUrl;
}
