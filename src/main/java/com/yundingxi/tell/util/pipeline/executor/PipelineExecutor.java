package com.yundingxi.tell.util.pipeline.executor;

import com.yundingxi.tell.util.pipeline.context.Context;
import com.yundingxi.tell.util.pipeline.handle.ContextHandler;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * pipeline管道执行器，提供同步执行管道方法
 *
 * @author rayss
 */
public class PipelineExecutor<R> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 引用 PipelineRouteConfig 中的 pipelineRouteMap
     */
    @Resource
    private Map<Class<? extends Context>,
            List<? extends ContextHandler<? super Context>>> pipelineRouteMap;

    /**
     * 同步处理输入的上下文数据<br/>
     * 如果处理时上下文数据流通到最后一个处理器且最后一个处理器返回 true，则返回 true，否则返回 false
     *
     * @param context 输入的上下文数据
     * @return 处理过程中管道是否畅通，畅通返回 true，不畅通返回 false
     */
    public boolean acceptSync(Context context) {
        Objects.requireNonNull(context, "上下文数据不能为 null");
        // 获取对应上下文Context的Class对象
        Class<? extends Context> dataType = context.getClass();
        // 通过上面获取到的Class对象从容器中获取到对应的管道
        List<? extends ContextHandler<? super Context>> pipeline = pipelineRouteMap.get(dataType);

        if (CollectionUtils.isEmpty(pipeline)) {
            logger.error("{} 的管道为空", dataType.getSimpleName());
            return false;
        }

        // 管道是否畅通
        boolean lastSuccess = true;

        //遍历管道中的内容进行顺序执行
        for (ContextHandler<? super Context> handler : pipeline) {
            try {
                // 当前处理器处理数据，并返回是否继续向下处理
                lastSuccess = handler.handle(context);
            } catch (Throwable ex) {
                lastSuccess = false;
                logger.error("[{}] 处理异常，handler={}", context.getName(), handler.getClass().getSimpleName(), ex);
            }

            // 不再向下处理
            if (!lastSuccess) {
                break;
            }
        }
        return lastSuccess;
    }

    private final ThreadPoolExecutor pipelineThreadPool =
            new ThreadPoolExecutor(4, 8, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    /**
     * 异步执行任务，但是异步是以管道为单位
     *
     * @param context  上下文
     * @param callback 回调方法
     */
    public boolean acceptAsync(Context context, BiConsumer<Context, Boolean> callback) {
        AtomicBoolean lastSuccess = new AtomicBoolean(true);
        pipelineThreadPool.execute(() -> {
            //注意我这里实际上调用的是同步方法，是因为管道之间是异步，但是管道内依旧是同步
            boolean success = acceptSync(context);

            if (callback != null) {
                callback.accept(context, success);
            }
            lastSuccess.set(success);
        });
        return lastSuccess.get();
    }
}