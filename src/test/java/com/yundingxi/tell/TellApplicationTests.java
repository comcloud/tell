package com.yundingxi.tell;

import com.alibaba.fastjson.JSONObject;
import com.yundingxi.tell.bean.entity.*;
import com.yundingxi.tell.bean.vo.DiaryReturnVo;
import com.yundingxi.tell.bean.vo.TimelineVo;
import com.yundingxi.tell.common.ResourceInit;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.*;
import com.yundingxi.tell.service.*;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

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
    @Autowired
    private StampMapper stampMapper;
    @Autowired
    private AchieveMapper achieveMapper;

    @Autowired
    private ResourceInit resourceInit;

    @Autowired
    private LetterService letterService;

    @Test
    void contextLoads() throws IllegalAccessException {
        JSONObject jsonObject = (JSONObject) redisUtil.get("listener:" + "oUGur5NFcTHkjrPDDnRpSEGDVX5s" + ":offset");
        System.out.println(jsonObject.toJSONString());
        @SuppressWarnings("unchecked") ArrayList<String> list = (ArrayList<String>) jsonObject.get("diary");
        System.out.println(list);
//        for (int i = 0; i < 20; i++) {
//            System.out.println(UUID.randomUUID().toString());
//        }
    }

    private void giveAllAchieveAndAllStamp(String openId) {
        List<Stamp> stamps = stampMapper.selectAllStamp();
        List<Achieve> achieves = achieveMapper.selectAllAchieve();
        achieves.forEach(achieve -> {
            achieveMapper.insertSingleNewUserAchieve(new UserAchieve(UUID.randomUUID().toString(), openId, achieve.getId(), new Date(), "0"));
        });
        stamps.forEach(stamp -> {
            stampMapper.insertSingleNewUserStamp(new UserStamp(UUID.randomUUID().toString(), stamp.getId(), openId, "0", new Date(), 0));
        });
    }

    private List<UserStamp> getUserStamps(String openId) {
        //每个人赋予默认邮票
        List<Stamp> baseStamp = stampMapper.selectBaseStamp();
        List<UserStamp> userStampList = new ArrayList<>();
        baseStamp.forEach(stamp -> userStampList.add(new UserStamp(UUID.randomUUID().toString(), stamp.getId(), openId, "1", new Date(), 1)));
        return userStampList;
    }

    void giveEveryoneToDefaultStamp() {
        List<String> allOpenId = userMapper.selectAllOpenId();
        allOpenId.forEach(openId -> {
            List<String> strings = Arrays.asList("fb0c3579-d942-4de4-93df-19e59a48e9f7",
                    "4a1c478f-bfa2-4339-902e-82653bdc178e",
                    "bd7d85e5-042e-40a1-8a3f-bf045028df40",
                    "01942371-da36-40e5-8fd9-327acba861b9");
            List<UserStamp> userStampList = new ArrayList<>(7);
            strings.forEach(s1 -> userStampList.add(new UserStamp(UUID.randomUUID().toString(), s1, openId, "1", new Date(), 1)));
            stampService.insertDefaultStamp(userStampList);

        });
    }

    void updateRedis() {
        long start = System.currentTimeMillis();
        List<String> allOpenId = userMapper.selectAllOpenId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        allOpenId.forEach(openId -> {
            del(openId);

            List<Letter> letterList = letterMapper.selectAllLetterByOpenIdNonState(openId, 4);
            letterList.forEach(letter -> {
                update(openId, "letter", sdf.format(letter.getReleaseTime()), letter.getContent());
            });

            List<Diarys> diarysList = diaryMapper.selectAllDiaryForSelfNonState(openId, "4");
            diarysList.forEach(diarys -> {
                update(openId, "diary", sdf.format(diarys.getDate()), diarys.getContent());
            });

//            List<SpittingGrooves> spittingGrooves = spittingGroovesMapper.selectAllSpitForSelfNonState(openId, "4");
//            spittingGrooves.forEach(spittingGrooves1 -> {
//                update(openId, "spit", sdf.format(spittingGrooves1.getDate()), spittingGrooves1.getContent());
//            });

        });
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }

    void update(String openId, String eventType, String time, String content) {
        String timelineKey = "user:" + openId + ":timeline";
        @SuppressWarnings("unchecked") LinkedList<TimelineVo> timelineVoLinkedList = (LinkedList<TimelineVo>) redisUtil.get(timelineKey);
        TimelineVo timelineVo = new TimelineVo(openId, eventType, time, content);
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
    }

    @Test
    void getAllStamp() {

    }

    @Test
    void getAllAchieve() {
    }

}
