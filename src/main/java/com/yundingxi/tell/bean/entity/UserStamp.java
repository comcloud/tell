package com.yundingxi.tell.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @version v1.0
 * @ClassName UserStamp
 * @Author rayss
 * @Datetime 2021/5/13 11:28 下午
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStamp {
    private String id;
    private String stampId;
    private String openId;
    private String state;
    private Date obtainTime;
    private int number;
}
