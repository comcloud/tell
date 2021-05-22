package com.yundingxi.tell.bean.vo.submessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订阅消息实体类
 * @version v1.0
 * @ClassName SubMessageVo
 * @Author rayss
 * @Datetime 2021/5/17 2:23 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubMessageVo {
    private String accessToken;
    private String touser;
    private String templateId;
    private String page;
    private SubMessageCommentDataVo data;
    private String miniProgramState;
}
