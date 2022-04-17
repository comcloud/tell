package com.yundingxi.model.vo.submessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName SubMessageReplyVo
 * @Author rayss
 * @Datetime 2021/5/22 3:32 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubMessageReplyVo {
    private SubMessageValueVo thing1;
    private SubMessageValueVo time2;
    private SubMessageValueVo thing3;
}
