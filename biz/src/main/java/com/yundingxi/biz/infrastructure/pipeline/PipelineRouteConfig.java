package com.yundingxi.biz.infrastructure.pipeline;

import com.yundingxi.biz.infrastructure.pipeline.context.Context;
import com.yundingxi.biz.infrastructure.pipeline.context.UserDataAnalysisContext;
import com.yundingxi.biz.infrastructure.pipeline.handle.*;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author rayss
 */
public class PipelineRouteConfig implements ApplicationContextAware {

    private ApplicationContext appContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    //真正的路由表实现，使用Map存储
    //key : Context的实现类Class对象
    //value : 一个个List，其中存储的就是处理器 ，也就是一个个管道
    private static final
    Map<Class<? extends Context>,
            List<Class<? extends ContextHandler<? extends Context>>>> PIPELINE_ROUTE_MAP = new HashMap<>(4);

    //使用静态代码块对Map进行初始化
    static {
        PIPELINE_ROUTE_MAP.put(UserDataAnalysisContext.class,
                //用户来数据分析等的管道，管道中内容有：回顾历史，信件、日记、吐槽内容分析
                Arrays.asList(
                        ReviewContextHandler.class,
                        LetterAnalysisContextHandler.class,
                        DiaryAnalysisContextHandler.class,
                        SpitAnalysisContextHandler.class
                ));
        // 将来其他 Context 的管道配置
    }

    /**
     * 路由表存储的是对应的Class对象，通过这个方法通过ApplicationContext获取到对应的实例化对象
     * @return 获取对应的Map
     */
    @Bean("pipelineRouteMap")
    public Map<Class<? extends Context>, List<? extends ContextHandler<? extends Context>>> getHandlerPipelineMap() {
        return PIPELINE_ROUTE_MAP.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::toPipeline));
    }

    /**
     * 根据给定的管道中 ContextHandler 的类型的列表，构建管道
     */
    private List<? extends ContextHandler<? extends Context>> toPipeline(
            Map.Entry<Class<? extends Context>, List<Class<? extends ContextHandler<? extends Context>>>> entry) {
        return entry.getValue()
                .stream()
                .map(appContext::getBean)
                .collect(Collectors.toList());
    }


}