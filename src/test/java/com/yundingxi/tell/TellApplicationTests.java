package com.yundingxi.tell;

import cn.hutool.core.bean.BeanUtil;
import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.entity.Diarys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TellApplicationTests {
    @Test
    void contextLoads() {

    }

    @Test
    void fileUtilTest() {
        DiaryDto diaryDto = DiaryDto.builder().content("content").openId("open id").weather("weather").penName("pen name").build();

        Diarys diarys = BeanUtil.toBean(diaryDto, Diarys.class);
        System.out.println(diarys);
    }
}
