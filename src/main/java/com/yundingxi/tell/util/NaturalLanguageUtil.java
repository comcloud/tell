package com.yundingxi.tell.util;

import com.baidu.aip.nlp.AipNlp;
import com.fasterxml.jackson.databind.JsonNode;
import com.yundingxi.tell.util.baidu.NlpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * 自然语言分析工具类
 *
 * @version v1.0
 * @ClassName NaturalLanguageUtil
 * @Author rayss
 * @Datetime 2021/4/24 11:24 上午
 */

public class NaturalLanguageUtil {

    private static final Logger log = LoggerFactory.getLogger(NaturalLanguageUtil.class);

    /** 积极对应的数字*/
    private static final Integer POSITIVE_SENTIMENT = 2;
    /** 中性对应的数字*/
    private static final Integer NEUTRAL_SENTIMENT = 1;
    /** 消极对应的数字*/
    private static final Integer NEGATIVE_SENTIMENT = 0;
    /** 可信度阈值[0-1]*/
    private static final double CONFIDENCE_THRESHOLD = 0.7;
    /** 积极性阈值[0-1]*/
    private static final double POSITIVE_THRESHOLD = 0.2;


    public static Integer emotionAnalysis(String content) {
        AipNlp client = NlpClient.getClient();
        HashMap<String, Object> options = new HashMap<>(4);
        JSONObject lexer = client.sentimentClassify(content, options);
        log.info(lexer.toString());
        JsonNode parseJson = JsonUtil.parseJson(lexer.toString());
        int sentiment = parseJson.findPath("sentiment").asInt();
        double confidence = parseJson.findPath("confidence").asDouble();
        double positiveProv = parseJson.findPath("positive_prob").asDouble();
        if(POSITIVE_SENTIMENT.equals(sentiment) || NEUTRAL_SENTIMENT.equals(sentiment)){
            return sentiment + 1;
        }else if(NEGATIVE_SENTIMENT.equals(sentiment)
                && confidence > CONFIDENCE_THRESHOLD
                && positiveProv < POSITIVE_THRESHOLD){
            return 0;
        }
        return 1;
    }
}
