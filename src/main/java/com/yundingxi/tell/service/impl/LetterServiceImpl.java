package com.yundingxi.tell.service.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @version v1.0
 * @ClassName LetterServiceImpl
 * @Author rayss
 * @Datetime 2021/3/24 6:31 下午
 */

@Service
public class LetterServiceImpl implements LetterService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private LetterMapper letterMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String saveSingleLetter(Letter letter) {
        letter.setId(UUID.randomUUID().toString());
        letter.setState(1);
        letterMapper.insertSingleLetter(letter);
        return JsonNodeFactory.instance.objectNode().put("sendTime", 0).toPrettyString();
    }

    @Override
    public String putUnreadMessage(String openId) {
        //使用redis获取
        return (String) redisUtil.get(openId);
    }

    @Override
    public List<Letter> getLettersByOpenId(String openId) {
        return null;
    }
}
