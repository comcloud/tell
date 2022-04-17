package com.yundingxi.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName IndexLetterVo
 * @Author rayss
 * @Datetime 2021/4/13 12:51 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexLetterVo {
    private String openId;
    private String letterId;
}
