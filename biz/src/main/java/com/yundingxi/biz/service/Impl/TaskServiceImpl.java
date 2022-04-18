package com.yundingxi.biz.service.Impl;


import com.alibaba.fastjson.JSONObject;
import com.yundingxi.biz.service.AchieveService;
import com.yundingxi.biz.service.TaskService;
import com.yundingxi.common.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final AchieveService achieveService;

    private final RedisUtil redisUtil;

    public TaskServiceImpl(AchieveService achieveService, RedisUtil redisUtil) {
        this.achieveService = achieveService;
        this.redisUtil = redisUtil;
    }

    /**
     * @param openId 用户open id
     * @param isForce 是否强制进行初始化，true表示即使用户存在缓存但是依旧重新初始化
     */
    @Override
    public void stampAndAchieveInitForEveryone(String openId, boolean isForce) {
        List<String> achieveTypeList = achieveService.selectAllAchieveType();
        String offsetKey = "listener:" + openId + ":offset";
        if (redisUtil.get(offsetKey) == null || isForce) {
            JSONObject jsonObject = new JSONObject();
            achieveTypeList.forEach(achieveType -> jsonObject.put(achieveType, new ArrayList<String>(5)));
            redisUtil.set(offsetKey, jsonObject);
        }
    }
}
