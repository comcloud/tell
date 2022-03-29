package com.yundingxi.tell.util.pipeline.handle;

import com.yundingxi.tell.bean.vo.ProfileNumVo;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import com.yundingxi.tell.util.pipeline.context.UserDataAnalysisContext;
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
