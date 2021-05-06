package com.yundingxi.tell.bean.vo;

import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.dto.IndexLetterDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version v1.0
 * @ClassName HistoryDataVo
 * @Author rayss
 * @Datetime 2021/5/5 9:17 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "历史数据",description = "存储历史发布数据实体类")
public class HistoryDataVo {
    @ApiModelProperty("信件集合")
    private List<IndexLetterDto> letterList;
    @ApiModelProperty("日记集合")
    private List<DiaryDto> diaryList;
    @ApiModelProperty("吐槽集合")
    private List<SpittingGroovesVo> spittingGroovesList;
}
