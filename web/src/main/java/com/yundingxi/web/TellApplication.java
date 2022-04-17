package com.yundingxi.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author rayss
 */
@SpringBootApplication
@EnableScheduling
@ComponentScans(@ComponentScan(basePackages = {
        "com.yundingxi.biz", "com.yundingxi.web", "com.yundingxi.dao", "com.yundingxi.web"})
)
public class TellApplication {

    public static void main(String[] args) {
        SpringApplication.run(TellApplication.class, args);
    }

}
