package com.yundingxi.tell.common;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.AchieveMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.LetterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

    private final AchieveMapper achieveMapper;
    private final LetterService letterService;

    @Autowired
    public ResourceInit(RedisUtil redisUtil, UserMapper userMapper, LetterService letterService, AchieveMapper achieveMapper) {
        this.redisUtil = redisUtil;
        this.userMapper = userMapper;
        this.letterService = letterService;
        this.achieveMapper = achieveMapper;
    }

    @Override
    public void run(String... args) {
        log.info("项目初始化");
        List<String> openIdList = userMapper.selectAllOpenId();
        letterInitForEveryOpenId(openIdList);
        stampAndAchieveInitForEveryone(openIdList);
    }

    public void letterInitForEveryOpenId(List<String> openIdList) {
        openIdList.forEach(letterService::setLetterInitInfoByOpenId);
    }

    /**
     * 邮票成就初始化，初始化内容，加载出每一个人的基本信息
     */
    public void stampAndAchieveInitForEveryone(List<String> openIdList) {
        List<String> achieveTypeList = achieveMapper.selectAllAchieveType();
        openIdList.forEach(openId -> {
            String offsetKey = "listener:" + openId + ":offset";
            if (redisUtil.get(offsetKey) == null) {
                ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
                achieveTypeList.forEach(achieveType -> objectNode.put(achieveType, 0));
                redisUtil.set(offsetKey, objectNode.toPrettyString());
            }
        });
    }
}
