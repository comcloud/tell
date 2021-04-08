package com.yundingxi.tell;

import java.util.Date;

import cn.hutool.core.bean.BeanUtil;
import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.mapper.CommentsMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.service.DiaryService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void contextLoads() {
//        SpittingGrooves lllll = new SpittingGrooves("00001","...0000.",new Date(),"1","s","002","","","");
//        service.insert(lllll);
//        s.insert(lllll);
//        s.updateById(lllll);
//            s.deleteById("001");
//        System.out.println(s.selectDetailsById("001"));
//        System.out.println(a.selectAll("ab30fc9c-aabd-48f5-8e98-2a701052bffc"));
//        for (int i = 0; i < 20; i++) {
//            DiaryDto diaryDto = new DiaryDto("System.out.println(a.selectAll(\"ab30fc9c-aabd-48f5-8e98-2a701052bffc\"));", "小布丁", "阴天", "vx002", (i % 2) + "");
//            diaryService.saveDiary(diaryDto);
//        }
    }


    @Test
    void fileUtilTest() {
        DiaryDto diaryDto = DiaryDto.builder().content("content").openId("open id").weather("weather").penName("pen name").build();

        Diarys diarys = BeanUtil.toBean(diaryDto, Diarys.class);
        System.out.println(diarys);
    }
}
