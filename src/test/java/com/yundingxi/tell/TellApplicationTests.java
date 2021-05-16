package com.yundingxi.tell;

import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
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
import java.util.LinkedList;
import java.util.List;

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
    }
    void updateRedis(){
        long start = System.currentTimeMillis();
        List<String> allOpenId = userMapper.selectAllOpenId();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        allOpenId.forEach(openId -> {
            del(openId);
            List<Letter> letterList = letterMapper.selectAllLetterByOpenIdNonState(openId, 4);
            if("oUGur5GBjDC1B3z-brhlM9rL3Gnc".equals(openId)){
                System.out.println("letterList.size() = " + letterList.size());
            }
            System.out.println("letterList.size() = " + letterList.size());
            letterList.forEach(letter -> {
                update(openId,"letter",sdf.format(letter.getReleaseTime()));
            });
            List<Diarys> diarysList = diaryMapper.selectAllDiaryForSelfNonState(openId,"4");
            if("oUGur5GBjDC1B3z-brhlM9rL3Gnc".equals(openId)){
                System.out.println("diarysList.size() = " + diarysList.size());
            }
            diarysList.forEach(diarys -> {
                update(openId,"diary",sdf.format(diarys.getDate()));
            });
            List<SpittingGrooves> spittingGrooves = spittingGroovesMapper.selectAllSpitForSelfNonState(openId,"4");
            if("oUGur5GBjDC1B3z-brhlM9rL3Gnc".equals(openId)){
                System.out.println("spittingGrooves.size() = " + spittingGrooves.size());
            }
            spittingGrooves.forEach(spittingGrooves1 -> {
                update(openId,"spit",sdf.format(spittingGrooves1.getDate()));
            });

        });
        System.out.println((System.currentTimeMillis() - start) + "ms");
    }
    void update(String openId,String eventType,String time){
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
    void del(String openId){
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
