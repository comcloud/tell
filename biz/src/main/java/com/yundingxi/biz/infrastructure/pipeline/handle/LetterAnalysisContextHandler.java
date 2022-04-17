package com.yundingxi.biz.infrastructure.pipeline.handle;

import com.yundingxi.biz.util.GeneralDataProcessUtil;
import com.yundingxi.biz.infrastructure.pipeline.context.UserDataAnalysisContext;
import com.yundingxi.dao.mapper.LetterMapper;
import com.yundingxi.model.vo.ProfileNumVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version v1.0
 * @ClassName LetterAnalysisContextHandler
 * @Author rayss
 * @Datetime 2021/7/22 2:26 下午
 */

@Component
public class LetterAnalysisContextHandler implements ContextHandler<UserDataAnalysisContext> {

    private final LetterMapper letterMapper;

    public LetterAnalysisContextHandler(LetterMapper letterMapper) {
        this.letterMapper = letterMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<String> letterContentList = letterMapper.selectAllLetterContentByOpenId(context.getOpenId(), context.getCurrentTime());
        List<ProfileNumVo> profileNumVos = GeneralDataProcessUtil.singleAnalysis(letterContentList);
        return context.getAnalysis().put("letter", profileNumVos) == null;
    }
}
