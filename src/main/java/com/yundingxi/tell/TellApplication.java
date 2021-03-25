package com.yundingxi.tell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author rayss
 */
@SpringBootApplication
@EnableScheduling
public class TellApplication {

    public static void main(String[] args) {
        SpringApplication.run(TellApplication.class, args);
    }

}
