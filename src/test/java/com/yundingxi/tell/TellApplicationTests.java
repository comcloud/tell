package com.yundingxi.tell;

import com.yundingxi.tell.bean.entity.Achieve;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.vo.DiaryReturnVo;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.CommentsMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.service.AchieveService;
import com.yundingxi.tell.service.DiaryService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.service.StampService;
import com.yundingxi.tell.util.GeneralDataProcessUtil;
import org.apache.cxf.interceptor.AbstractOutDatabindingInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    @Test
    void contextLoads() throws IllegalAccessException {
        redisUtil.del();
    }


    @Test
    void fileUtilTest() {
        List<Diarys> allPublicDiary = diaryService.getAllPublicDiary();
        List<DiaryReturnVo> diaryReturnVos = GeneralDataProcessUtil.configDataFromList(allPublicDiary, Diarys.class, DiaryReturnVo.class);
        System.out.println(diaryReturnVos);
    }

    @Test
    void getAllStamp(){

        System.out.println("1111");
        System.out.println(stampService.getAllStamp("oUGur5B9FwaEIrLj_ftK4hURIsoE", 1));
    }
    @Test
    void getAllAchieve(){
        System.out.println(achieveService.getAllAchieve("oUGur5B9FwaEIrLj_ftK4hURIsoE", 1));
    }

}
