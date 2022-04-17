package com.yundingxi.web.configuration.websocket;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @author HP
 */
public class WebSocketInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{CustomWebSocketConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/websocket"};
    }
}