package com.yundingxi.tell.common;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.LetterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @version v1.0
 * @ClassName ResourceInit
 * @Author rayss
 * @Datetime 2021/3/25 8:55 下午
 */
@Component
public class ResourceInit implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(ResourceInit.class);

    private final RedisUtil redisUtil;

    private final UserMapper userMapper;

    private final LetterService letterService;
    @Autowired
    public ResourceInit(RedisUtil redisUtil, UserMapper userMapper,LetterService letterService) {
        this.redisUtil = redisUtil;
        this.userMapper = userMapper;
        this.letterService = letterService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("项目初始化");
        letterInitForEveryOpenId();
    }

    public void letterInitForEveryOpenId(){
        List<String> openIdList = userMapper.selectAllOpenId();
        openIdList.forEach(letterService::setLetterInitInfoByOpenId);
    }


}
