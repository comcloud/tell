package com.yundingxi.web.configuration.datasource;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 成都犀牛
 * @version version 1.0
 * @date 2020/9/24 17:34
 */
@Configuration
@AutoConfigureAfter(DruidConfiguration.class)
@MapperScan("com.yundingxi.dao.mapper")
public class MybatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.yundingxi.dao.mapper");
        return mapperScannerConfigurer;
    }
}