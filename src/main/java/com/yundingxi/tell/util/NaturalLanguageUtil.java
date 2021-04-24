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

    public static String emotionAnalysis(String content) {
        AipNlp client = NlpClient.getClient();
        HashMap<String, Object> options = new HashMap<>(4);
        JSONObject lexer = client.sentimentClassify(content, options);
        log.info(lexer.toString());
        JsonNode parseJson = JsonUtil.parseJson(lexer.toString());
        String sentiment = parseJson.findPath("sentiment").toString();
        String confidence = parseJson.findPath("confidence").toString();
        String positiveProv = parseJson.findPath("positive_prov").toString();
        String negativeProb = parseJson.findPath("negative_prob").toString();
        return sentiment;
    }
}
