package com.yundingxi.tell.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @version v1.0
 * @ClassName UserAchieve
 * @Author rayss
 * @Datetime 2021/5/13 11:34 下午
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAchieve {
    private String id;
    private String openId;
    private String achieveId;
    private Date obtainTime;
    private String state;
}
