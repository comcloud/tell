package com.yundingxi.tell.util.pipeline.handle;

import com.yundingxi.tell.bean.vo.ProfileNumVo;
import com.yundingxi.tell.mapper.DiaryMapper;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import com.yundingxi.tell.util.pipeline.context.UserDataAnalysisContext;
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
