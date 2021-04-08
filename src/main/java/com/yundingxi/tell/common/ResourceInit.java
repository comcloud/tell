package com.yundingxi.tell.common;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.UserMapper;
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

    @Autowired
    public ResourceInit(RedisUtil redisUtil, UserMapper userMapper) {
        this.redisUtil = redisUtil;
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("项目初始化");
        List<String> openIdList = userMapper.selectAllOpenId();
        openIdList.forEach(openId -> {
            ObjectNode letterInfo = JsonNodeFactory.instance.objectNode().putObject(openId + "_letter_info");
            String date = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            letterInfo.put("date", date);
            letterInfo.put("letter_count_location", 1);
            redisUtil.set(openId + "_letter_info", letterInfo.toPrettyString());
        });
    }
}
