package com.yundingxi.biz.infrastructure.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @version v1.0
 * @ClassName CustomInvocationHandler
 * @Author rayss
 * @Datetime 2021/6/26 4:47 下午
 */

public class CustomInvocationHandler implements InvocationHandler {

    /**
     * 增强对象
     */
    private final Object proxyObject;

    /**
     * 前置增强
     */
    private BeforeProcessor beforeProcessor;
    /**
     * 后置增强
     */
    private AfterProcessor afterProcessor;

    public CustomInvocationHandler(Object proxyObject) {
        this.proxyObject = proxyObject;
    }

    /**
     * 获取代理对象方法
     * @param beforeProcessor 前置增强
     * @param afterProcessor 后置增强
     * @return 增强后的代理对象
     */
    public Object getProxyObject(BeforeProcessor beforeProcessor, AfterProcessor afterProcessor) {
        this.beforeProcessor = beforeProcessor;
        this.afterProcessor = afterProcessor;
        return Proxy.newProxyInstance(CustomInvocationHandler.class.getClassLoader(), proxyObject.getClass().getInterfaces(), this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (beforeProcessor != null) {
            beforeProcessor.beforeProcess();
        }
        Object methodObject = method.invoke(proxyObject, args);
        if (afterProcessor != null) {
            afterProcessor.afterProcess();
        }
        return methodObject;
    }
}
