package com.yundingxi.biz.infrastructure.pipeline.handle;

import com.yundingxi.biz.infrastructure.pipeline.context.UserDataAnalysisContext;
import com.yundingxi.dao.mapper.LetterMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户数据回顾处理器
 *
 * @version v1.0
 * @ClassName ReviewContextHandler
 * @Author rayss
 * @Datetime 2021/7/22 11:46 上午
 */

@Component
public class ReviewContextHandler implements ContextHandler<UserDataAnalysisContext> {


    private final LetterMapper letterMapper;

    public ReviewContextHandler(LetterMapper letterMapper) {
        this.letterMapper = letterMapper;
    }

    @Override
    public boolean handle(UserDataAnalysisContext context) {
        List<List<String>> review = new ArrayList<>();
        review.add(Arrays.asList("发布数量", "解忧", "日记", "吐槽"));
        //表示有多少列
        int countOfColumn = review.get(0).size();
        for (int i = 0; i < countOfColumn; i++) {
            review.add(Arrays.asList("第" + (i + 1) + "周"
                    , letterMapper.selectWeeklyQuantityThroughOpenId(context.getOpenId(), context.getCurrentTime(), "letter", i, 7) + ""
                    , letterMapper.selectWeeklyQuantityThroughOpenId(context.getOpenId(), context.getCurrentTime(), "diarys", i, 7) + ""
                    , letterMapper.selectWeeklyQuantityThroughOpenId(context.getOpenId(), context.getCurrentTime(), "spitting_grooves", i, 7) + "")
            );
        }
        return context.getResult().setFirstValue(review) != null;
    }
}