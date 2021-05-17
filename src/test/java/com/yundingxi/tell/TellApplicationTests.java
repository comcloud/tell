package com.yundingxi.tell;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.bean.entity.*;
import com.yundingxi.tell.bean.vo.DiaryReturnVo;
import com.yundingxi.tell.bean.vo.TimelineVo;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.*;
import com.yundingxi.tell.service.AchieveService;
import com.yundingxi.tell.service.DiaryService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.service.StampService;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import com.yundingxi.tell.util.InternetUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        String accessToken = InternetUtil.getAccessToken();
        String id = "2c96f764-07c2-477b-8241-b559dcf69ccc";
        SpittingGrooves spittingGrooves = spittingGroovesMapper.selectOpenIdAndTitleById(id);
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.putObject("thing2").put("value", "内容");
        objectNode.putObject("thing3").put("value", userMapper.selectPenNameByOpenId("open id"));
        objectNode.putObject("thing4").put("value", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        objectNode.putObject("thing10").put("value", spittingGrooves.getTitle());

        System.out.println(objectNode.toPrettyString());
    }

    void giveEveryoneToDefaultStamp() {
        List<String> strings = Arrays.asList("1a5952ad-6078-4003-b997-510f14501933",
                "7937ead0-0298-40c6-bf05-fe006b060597",
                "9e53aa4e-ec94-4ef3-83a2-968971d6997d",
                "a6cb7443-bc66-42ab-9777-e5ff5479b475",
                "de2dd5d4-f159-4646-a0f9-af4449a1f995",
                "e433d3cd-e1c9-4649-8fd1-2599949d1ec1");
        List<UserStamp> userStampList = new ArrayList<>(7);
        strings.forEach(s1 -> userStampList.add(new UserStamp(UUID.randomUUID().toString(), s1, "oUGur5NFcTHkjrPDDnRpSEGDVX5s", "1", new Date(), 1)));
        stampService.insertDefaultStamp(userStampList);
    }

    void updateRedis() {
        long start = System.currentTimeMillis();
        List<String> allOpenId = userMapper.selectAllOpenId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        allOpenId.forEach(openId -> {
            del(openId);
            List<Letter> letterList = letterMapper.selectAllLetterByOpenIdNonState(openId, 4);
            if ("oUGur5GBjDC1B3z-brhlM9rL3Gnc".equals(openId)) {
                System.out.println("letterList.size() = " + letterList.size());
            }
            System.out.println("letterList.size() = " + letterList.size());
            letterList.forEach(letter -> {
                update(openId, "letter", sdf.format(letter.getReleaseTime()));
            });
            List<Diarys> diarysList = diaryMapper.selectAllDiaryForSelfNonState(openId, "4");
            if ("oUGur5GBjDC1B3z-brhlM9rL3Gnc".equals(openId)) {
                System.out.println("diarysList.size() = " + diarysList.size());
            }
            diarysList.forEach(diarys -> {
                update(openId, "diary", sdf.format(diarys.getDate()));
            });
            List<SpittingGrooves> spittingGrooves = spittingGroovesMapper.selectAllSpitForSelfNonState(openId, "4");
            if ("oUGur5GBjDC1B3z-brhlM9rL3Gnc".equals(openId)) {
                System.out.println("spittingGrooves.size() = " + spittingGrooves.size());
            }
            spittingGrooves.forEach(spittingGrooves1 -> {
                update(openId, "spit", sdf.format(spittingGrooves1.getDate()));
            });

        });
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }

    void update(String openId, String eventType, String time) {
        String timelineKey = "user:" + openId + ":timeline";
        @SuppressWarnings("unchecked") LinkedList<TimelineVo> timelineVoLinkedList = (LinkedList<TimelineVo>) redisUtil.get(timelineKey);
        TimelineVo timelineVo = new TimelineVo(openId, eventType, time);
        if (timelineVoLinkedList == null) {
            LinkedList<TimelineVo> timelineVos = new LinkedList<>();
            timelineVos.addFirst(timelineVo);
            redisUtil.set(timelineKey, timelineVos);
        } else {
            timelineVoLinkedList.addFirst(timelineVo);
            redisUtil.set(timelineKey, timelineVoLinkedList);
        }
    }

    void del(String openId) {
        String timelineKey = "user:" + openId + ":timeline";
        redisUtil.del(timelineKey);
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
