package com.yundingxi.tell.util;

import com.baidu.aip.client.BaseClient;
import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.nlp.AipNlp;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 积极对应的数字
     */
    private static final Integer POSITIVE_SENTIMENT = 2;
    /**
     * 中性对应的数字
     */
    private static final Integer NEUTRAL_SENTIMENT = 1;
    /**
     * 消极对应的数字
     */
    private static final Integer NEGATIVE_SENTIMENT = 0;
    /**
     * 可信度阈值[0-1]
     */
    private static final double CONFIDENCE_THRESHOLD = 0.7;
    /**
     * 积极性阈值[0-1]
     */
    private static final double POSITIVE_THRESHOLD = 0.2;


    /**
     * 文本情感分析
     *
     * @param content 分析内容
     * @return 分析结果，0表示愤怒，1表示低落，2表示温和，3表示愉悦
     */
    public static Integer emotionAnalysis(String content) {
        AipNlp client = NlpClient.getNlpClient();
        HashMap<String, Object> options = new HashMap<>(4);
        JSONObject lexer = client.sentimentClassify(content, options);
        log.info(lexer.toString());
        JsonNode parseJson = JsonUtil.parseJson(lexer.toString());
        int sentiment = parseJson.findPath("sentiment").asInt();
        double confidence = parseJson.findPath("confidence").asDouble();
        double positiveProv = parseJson.findPath("positive_prob").asDouble();
        if (POSITIVE_SENTIMENT.equals(sentiment) || NEUTRAL_SENTIMENT.equals(sentiment)) {
            return sentiment + 1;
        } else if (NEGATIVE_SENTIMENT.equals(sentiment)
                && confidence > CONFIDENCE_THRESHOLD
                && positiveProv < POSITIVE_THRESHOLD) {
            return 0;
        }
        return 1;
    }

    /**
     * 文本是否合法内容
     *
     * @param content 分析文本内容
     * @return 是否合法
     */
    public static boolean isTextLegal(String content) {
        AipContentCensor client = NlpClient.getContentCensorClient();

        JSONObject userDefined = client.textCensorUserDefined(content);
        JsonNode parseJson = JsonUtil.parseJson(userDefined.toString());
        log.info(parseJson.toPrettyString());
        JsonNode conclusionType = parseJson.findPath("conclusionType");
        return !"".equals(conclusionType.toString()) && conclusionType.asInt() == 1;
    }

    /**
     * 结果类型，1.合规，2.不合规，3.疑似，4.审核失败
     *
     * @param content 文本内容
     * @return 返回文本违规与否判断信息
     */
    public static Integer getTextLegalType(String content) {
        AipContentCensor client = NlpClient.getContentCensorClient();

        JSONObject userDefined = client.textCensorUserDefined(content);
        JsonNode parseJson = JsonUtil.parseJson(userDefined.toString());
        log.info(parseJson.toPrettyString());
        JsonNode conclusionType = parseJson.findPath("conclusionType");
        return "".equals(conclusionType.toString()) ? 4 : conclusionType.asInt();
    }


    /**
     * 百度NLP客户端内部类
     */
    static class NlpClient {

        private static final String APP_ID = "24055538";
        private static final String API_KEY = "Y0dsQ43da22joPf4PTLHu5o3";
        private static final String SECRET_KEY = "siQgyddmyqXIKUKYEsvHYr3ZQHWFgPo3";

        private static final Map<Class<? extends BaseClient>, BaseClient> AIP_MAP = new HashMap<>();

        /**
         * @return 获取AipNlp客户端
         */
        public static AipNlp getNlpClient() {
            //lazy load
            BaseClient baseClient = AIP_MAP.get(AipNlp.class);
            if (baseClient == null) {
                BaseClient client = getClient(AipNlp.class);
                AIP_MAP.put(AipNlp.class, client);
                baseClient = client;
            }
            return (AipNlp) baseClient;
        }

        /**
         * @return 获取AipContentCensor客户端
         */
        public static AipContentCensor getContentCensorClient() {
            //lazy load
            BaseClient baseClient = AIP_MAP.get(AipContentCensor.class);
            if (baseClient == null) {
                BaseClient client = getClient(AipContentCensor.class);
                AIP_MAP.put(AipContentCensor.class, client);
                baseClient = client;
            }
            return (AipContentCensor) baseClient;
        }

        private static <T> T getClient(Class<? extends BaseClient> c) {
            Map<String, Integer> options = new HashMap<>(2);
            options.put("connectionTimeout", 20000);
            options.put("socketTimeout", 60000);
            return getClient(c, options);
        }

        @SneakyThrows
        @SuppressWarnings("unchecked")
        private static <T> T getClient(Class<? extends BaseClient> c, Map<String, Integer> options) {
            BaseClient clazz;
            Constructor<?> constructor = c.getDeclaredConstructor(String.class, String.class, String.class);
            constructor.setAccessible(true);
            Object obj = constructor.newInstance(APP_ID, API_KEY, SECRET_KEY);
            if (obj instanceof BaseClient) {
                clazz = (BaseClient) obj;
            } else {
                throw new IllegalArgumentException("不可以使用BaseClient之外的类");
            }
            clazz.setConnectionTimeoutInMillis(options.get("connectionTimeout"));
            clazz.setSocketTimeoutInMillis(options.get("socketTimeout"));

            return (T) clazz;
        }
    }
}
