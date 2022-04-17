package com.yundingxi.web.component;

import com.alibaba.fastjson.JSONObject;
import com.yundingxi.biz.service.LetterService;
import com.yundingxi.common.redis.RedisUtil;
import com.yundingxi.dao.mapper.AchieveMapper;
import com.yundingxi.dao.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public ResourceInit(RedisUtil redisUtil, UserMapper userMapper, @Qualifier("upgradeLetterServiceImpl") LetterService letterService, AchieveMapper achieveMapper) {
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
        openIdList.forEach(openId -> stampAndAchieveInitForEveryone(openId,false));
    }

    /**
     * @param openId 用户open id
     * @param isForce 是否强制进行初始化，true表示即使用户存在缓存但是依旧重新初始化
     */
    public void stampAndAchieveInitForEveryone(String openId,boolean isForce) {
        List<String> achieveTypeList = achieveMapper.selectAllAchieveType();
        String offsetKey = "listener:" + openId + ":offset";
        if (redisUtil.get(offsetKey) == null || isForce) {
            JSONObject jsonObject = new JSONObject();
            achieveTypeList.forEach(achieveType -> jsonObject.put(achieveType, new ArrayList<String>(5)));
            redisUtil.set(offsetKey, jsonObject);
        }
    }
}
