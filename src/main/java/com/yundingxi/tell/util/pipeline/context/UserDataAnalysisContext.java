package com.yundingxi.tell.util.pipeline.context;

import com.yundingxi.tell.bean.vo.ProfileNumVo;
import com.yundingxi.tell.util.ModelUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rayss
 */
@Getter
@Setter
@Builder
public class UserDataAnalysisContext extends Context {

    /**
     * 用户 id
     */
    private String openId;


    /**
     * 当前时间
     */
    private String currentTime;

    /**
     * 用户回复信息以及历史分析数据
     */
    private ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>> result;

    /**
     * 数据分析内容
     */
    private Map<String, List<ProfileNumVo>> analysis;

    /**
     * 模型创建出错时的错误信息
     */
    private String errorMsg;

    // 其他参数

    @Override
    public String getName() {
        return "用户数据分析";
    }
}