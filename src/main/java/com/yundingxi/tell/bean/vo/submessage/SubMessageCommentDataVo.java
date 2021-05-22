package com.yundingxi.tell.bean.vo.submessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName SubMessageDataVo
 * @Author rayss
 * @Datetime 2021/5/17 3:29 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubMessageCommentDataVo {
    private SubMessageValueVo thing2;
    private SubMessageValueVo thing3;
    private SubMessageValueVo time4;
    private SubMessageValueVo thing10;
}
