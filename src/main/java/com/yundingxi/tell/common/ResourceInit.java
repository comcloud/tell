package com.yundingxi.tell.common;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName ResourceInit
 * @Author rayss
 * @Datetime 2021/3/25 8:55 下午
 */

public class ResourceInit implements CommandLineRunner {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        List<String> openIdList = userMapper.selectAllOpenId();
        openIdList.forEach(openId -> {
            ObjectNode letterInfo = JsonNodeFactory.instance.objectNode().putObject("letter_info");
            String date = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            letterInfo.put("date",date);
            letterInfo.put("letter_count_location",1);
            redisUtil.set(openId+"_letter_info", letterInfo.toPrettyString());
        });
    }
}
