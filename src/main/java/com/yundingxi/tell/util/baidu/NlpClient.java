package com.yundingxi.tell.util.baidu;

import com.baidu.aip.nlp.AipNlp;
import org.springframework.beans.factory.annotation.Autowired;

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

    public static AipNlp getClient(){
        AipNlp client = new AipNlp(APP_ID,API_KEY,SECRET_KEY);

        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return client;
    }
}
