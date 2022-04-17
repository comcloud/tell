package com.yundingxi.biz.infrastructure.pipeline.handle;

import com.yundingxi.biz.util.GeneralDataProcessUtil;
import com.yundingxi.biz.infrastructure.pipeline.context.UserDataAnalysisContext;
import com.yundingxi.dao.mapper.SpittingGroovesMapper;
import com.yundingxi.model.vo.ProfileNumVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version v1.0
 * @ClassName SpitAnalysisContextHandler
 * @Author rayss
 * @Datetime 2021/7/22 11:49 上午
 */
@Component
public class SpitAnalysisContextHandler implements ContextHandler<UserDataAnalysisContext> {

    private final SpittingGroovesMapper spittingGroovesMapper;

    public SpitAnalysisContextHandler(SpittingGroovesMapper spittingGroovesMapper) {
        this.spittingGroovesMapper = spittingGroovesMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<String> spittingGroovesContentList = spittingGroovesMapper.selectAllSpitContentByOpenId(context.getOpenId(), context.getCurrentTime());
        List<ProfileNumVo> profileNumVos = GeneralDataProcessUtil.singleAnalysis(spittingGroovesContentList);
        return context.getAnalysis().put("spit_groove", profileNumVos) == null;
    }
}
