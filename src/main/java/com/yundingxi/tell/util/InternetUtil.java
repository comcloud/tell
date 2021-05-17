package com.yundingxi.tell.util;

import cn.hutool.http.HttpUtil;
import com.yundingxi.tell.bean.vo.OpenIdVo;

/**
 * @version v1.0
 * @ClassName HttpUtil
 * @Author rayss
 * @Datetime 2021/5/17 2:27 下午
 */

public class InternetUtil {
    private static final String SUB_MESSAGE_URL_GET = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    private static final OpenIdVo OPEN_ID_VO = (OpenIdVo) SpringUtil.getBean("openIdVo");

    public static String getAccessToken(){
        String url = SUB_MESSAGE_URL_GET+"&appid="+OPEN_ID_VO.getAppid()+"&secret="+OPEN_ID_VO.getSecret();
        String subInternetMessage = HttpUtil.get(url);
        return JsonUtil.parseJson(subInternetMessage).findPath("access_token").toString();
    }

}
