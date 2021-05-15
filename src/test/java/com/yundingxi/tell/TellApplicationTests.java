package com.yundingxi.tell;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.DiaryReturnVo;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import com.yundingxi.tell.bean.vo.TimelineVo;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.*;
import com.yundingxi.tell.service.AchieveService;
import com.yundingxi.tell.service.DiaryService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.service.StampService;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TellApplicationTests {
    @Autowired
    SpittingGroovesMapper s;
    @Autowired
    CommentsMapper a;
    @Autowired
    SpittingGroovesService service;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private RedisUtil redisUtil;


    @Autowired
    private StampService stampService;
    @Autowired
    private AchieveService achieveService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LetterMapper letterMapper;
    @Autowired
    private DiaryMapper diaryMapper;
    @Autowired
    private SpittingGroovesMapper spittingGroovesMapper;

    @Test
    void contextLoads() throws IllegalAccessException {

    }

    @Test
    void fileUtilTest() {
        List<Diarys> allPublicDiary = diaryService.getAllPublicDiary();
        List<DiaryReturnVo> diaryReturnVos = GeneralDataProcessUtil.configDataFromList(allPublicDiary, Diarys.class, DiaryReturnVo.class);
        System.out.println(diaryReturnVos);
    }

    @Test
    void getAllStamp() {

    }

    @Test
    void getAllAchieve() {
    }

}
