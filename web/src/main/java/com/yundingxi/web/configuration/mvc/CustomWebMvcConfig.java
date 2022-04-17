package com.yundingxi.web.configuration.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @version v1.0
 * @ClassName CustomWebMvcConfig
 * @Author rayss
 * @Datetime 2021/4/2 12:05 下午
 */

@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/","doc.html");
    }
}
