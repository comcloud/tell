package com.yundingxi.tell;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
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
//        updateRedis();
//        giveEveryoneToDefaultStamp();
    }

    void giveEveryoneToDefaultStamp() {
        //每个人赋予默认邮票
        List<String> strings = Arrays.asList("bd7d85e5-042e-40a1-8a3f-bf045028df40",
                "01942371-da36-40e5-8fd9-327acba861b9",
                "4a1c478f-bfa2-4339-902e-82653bdc178e",
                "fb0c3579-d942-4de4-93df-19e59a48e9f7");
        List<String> allOpenId = userMapper.selectAllOpenId();
        allOpenId.forEach(openId -> {
            List<UserStamp> userStampList = new ArrayList<>(4);
            strings.forEach(s1 -> {
                userStampList.add(new UserStamp(UUID.randomUUID().toString(),s1,openId,"1",new Date(),1));
            });
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
