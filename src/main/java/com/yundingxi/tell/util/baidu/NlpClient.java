package com.yundingxi.tell.util.baidu;

import com.baidu.aip.client.BaseClient;
import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.nlp.AipNlp;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName NlpClient
 * @Author rayss
 * @Datetime 2021/4/24 12:40 下午
 */

public class NlpClient {

    private static final String APP_ID = "24055538";
    private static final String API_KEY = "Y0dsQ43da22joPf4PTLHu5o3";
    private static final String SECRET_KEY = "siQgyddmyqXIKUKYEsvHYr3ZQHWFgPo3";

    private static final Map<Class<? extends BaseClient>, BaseClient> AIP_MAP = new HashMap<>();

    public static AipNlp getNlpClient() {
        //lazy load
        BaseClient baseClient = AIP_MAP.get(AipNlp.class);
        if (baseClient == null) {
            AIP_MAP.put(AipNlp.class, getClient(AipNlp.class));
        }
        return (AipNlp) baseClient;
    }

    public static AipContentCensor getContentCensorClient() {
        //lazy load
        BaseClient baseClient = AIP_MAP.get(AipContentCensor.class);
        if (baseClient == null) {
            AIP_MAP.put(AipContentCensor.class, getClient(AipContentCensor.class));
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
