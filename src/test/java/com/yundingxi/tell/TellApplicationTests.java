package com.yundingxi.tell;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.yundingxi.tell.bean.dto.DiaryDto;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.mapper.CommentsMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.service.DiaryService;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.util.FileUtil;
import com.yundingxi.tell.util.JsonUtil;
import com.yundingxi.tell.util.NaturalLanguageUtil;
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
//        if (NaturalLanguageUtil.isTextLegal("非常难受")) {
//            System.out.println("合规");
//        } else {
//            System.out.println("不合规");
//        }
        String jsonTest = "{\n" +
                "  \"conclusion\" : \"合规\",\n" +
                "  \"log_id\" : 16200325547424838,\n" +
                "  \"isHitMd5\" : false,\n" +
                "  \"conclusionType\" : 1\n" +
                "}";

        System.out.println(JsonUtil.parseJson(jsonTest).findPath("error").toString().equals(""));

    }


    @Test
    void fileUtilTest() {
        String jsonData = "{\n" +
                "  \"0ab39c3e-56b9-44c3-b813-2a25ad58547\":10,\n" +
                "  \"0ab39c3e-56b9-44c3-b813-2a25ad5f547\":10,\n" +
                "  \"0ab39c3e-56b9-44c3-b813-2a25ad5f847\":10,\n" +
                "  \"0ab39c3e-56b9-44c3-b813-2a25ad5f857\":10,\n" +
                "  \"0ab39c3e-56b9-44c3-b813-2a25ad5f854\":10\n" +
                "}";
        for (JsonNode jsonNode : JsonUtil.parseJson(jsonData)) {
            System.out.println(jsonNode.toString());
        }
    }
}
