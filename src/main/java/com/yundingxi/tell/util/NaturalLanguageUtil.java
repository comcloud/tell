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

    private static final String POSITIVE_SENTIMENT = "2";
    private static final String NEUTRAL_SENTIMENT = "1";
    private static final String NEGATIVE_SENTIMENT = "0";
    private static final double CONFIDENCE_THRESHOLD = 0.7;
    private static final double POSITIVE_THRESHOLD = 0.2;


    public static Integer emotionAnalysis(String content) {
        AipNlp client = NlpClient.getClient();
        HashMap<String, Object> options = new HashMap<>(4);
        JSONObject lexer = client.sentimentClassify(content, options);
        log.info(lexer.toString());
        JsonNode parseJson = JsonUtil.parseJson(lexer.toString());
        String sentiment = parseJson.findPath("sentiment").toString();
        String confidence = parseJson.findPath("confidence").toString();
        String positiveProv = parseJson.findPath("positive_prov").toString();
        if(POSITIVE_SENTIMENT.equals(sentiment) || NEUTRAL_SENTIMENT.equals(sentiment)){
            return Integer.parseInt(sentiment) + 1;
        }else if(NEGATIVE_SENTIMENT.equals(sentiment)
                && Double.parseDouble(confidence) > CONFIDENCE_THRESHOLD
                && Double.parseDouble(positiveProv) < POSITIVE_THRESHOLD){
            return 0;
        }
        return 1;
    }
}
