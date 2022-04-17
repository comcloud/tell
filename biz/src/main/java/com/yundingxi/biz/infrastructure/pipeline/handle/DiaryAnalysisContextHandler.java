package com.yundingxi.biz.infrastructure.pipeline.handle;

import com.yundingxi.biz.util.GeneralDataProcessUtil;
import com.yundingxi.biz.infrastructure.pipeline.context.UserDataAnalysisContext;
import com.yundingxi.dao.mapper.DiaryMapper;
import com.yundingxi.model.vo.ProfileNumVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version v1.0
 * @ClassName DiaryAnalysisContextHandler
 * @Author rayss
 * @Datetime 2021/7/22 2:25 下午
 */
@Component
public class DiaryAnalysisContextHandler implements ContextHandler<UserDataAnalysisContext>{

    private final DiaryMapper diaryMapper;

    public DiaryAnalysisContextHandler(DiaryMapper diaryMapper) {
        this.diaryMapper = diaryMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<String> diaryContentList = diaryMapper.selectAllDiaryContentByOpenId(context.getOpenId(), context.getCurrentTime());
        List<ProfileNumVo> profileNumVos = GeneralDataProcessUtil.singleAnalysis(diaryContentList);
        return context.getAnalysis().put("diary",profileNumVos) == null;
    }
}
