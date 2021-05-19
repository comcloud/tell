package com.yundingxi.tell.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.bean.dto.IndexLetterDto;
import com.yundingxi.tell.bean.dto.WeChatEnum;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.*;
import com.yundingxi.tell.mapper.UserMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @version v1.0
 * @ClassName GeneralDataProcessUtil
 * @Author rayss
 * @Datetime 2021/5/5 9:38 下午
 */

public class GeneralDataProcessUtil {

    private static final UserMapper USER_MAPPER = (UserMapper) SpringUtil.getBean("userMapper");

    public static void configLetterDataFromSingleObject(Letter letter, String openId, List<IndexLetterDto> letterDtoList) {
        letterDtoList.add(new IndexLetterDto(letter.getContent().length() > 50 ? letter.getContent().substring(0, 50) : letter.getContent(), letter.getOpenId(), letter.getId(), letter.getPenName(), letter.getStampUrl(), USER_MAPPER.selectPenNameByOpenId(openId), letter.getReleaseTime()));
    }


    public static List<IndexLetterDto> configLetterDataFromList(List<Letter> letterList, String openId) {
        List<IndexLetterDto> letterDtoList = new ArrayList<>();
        letterList.forEach(letter -> configLetterDataFromSingleObject(letter, openId, letterDtoList));
        return letterDtoList;
    }

    public static List<DiaryReturnVo> configDiaryDataFromList(List<Diarys> diaryList) {
        List<DiaryReturnVo> diaryDtoList = new ArrayList<>();
        diaryList.forEach(diary -> diaryDtoList.add(new DiaryReturnVo(diary.getId(), diary.getContent().length() > 60 ? diary.getContent().substring(0, 60) : diary.getContent(), diary.getPenName(), diary.getWeather(), diary.getOpenId(), diary.getState(), diary.getNumber(), diary.getDate())));
        return diaryDtoList;
    }

    public static List<SpittingGroovesVo> configSpitDataFromList(List<SpittingGrooves> spittingGroovesList) {
        List<SpittingGroovesVo> spittingGroovesVoList = new ArrayList<>();
        spittingGroovesList.forEach(spit -> spittingGroovesVoList.add(new SpittingGroovesVo(spit.getId(), spit.getNumber(), "", spit.getAvatarUrl(), spit.getPenName())));
        return spittingGroovesVoList;
    }

    /**
     * 1.获取R的所有域field，然后遍历
     * 2.根据遍历的field从data中获取内容
     * 3.将内容设置到创建的R对象中
     *
     * @param dataList   数据来源集合
     * @param paramType  数据来源集合的类型Class对象
     * @param resultType 返回值集合的类型Class对象
     * @param <R>        返回值类型
     * @param <P>        参数类型
     * @return 配置数据的集合
     */
    public static <R, P> List<R> configDataFromList(List<P> dataList, Class<? extends P> paramType, Class<? extends R> resultType) {
        List<R> resultList = new ArrayList<>();
        dataList.forEach(data -> {
            try {
                R r = resultType.newInstance();
                Field[] fields = resultType.getDeclaredFields();
                for (Field field : fields) {
                    Field declaredField = paramType.getDeclaredField(field.getName());
                    declaredField.setAccessible(true);
                    Object obj = declaredField.get(data);
                    field.setAccessible(true);
                    if (Modifier.isFinal(field.getModifiers())) {
                        continue;
                    }
                    if ("content".equals(declaredField.getName()) && paramType == Diarys.class) {
                        configReasonableString(obj, field, r);
                        continue;
                    } else if ("content".equals(declaredField.getName()) && paramType == Letter.class) {
                        configReasonableString(obj, field, r);
                        continue;
                    }
                    field.set(r, obj);
                }
                resultList.add(r);
            } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        });
        return resultList;
    }

    /**
     * @param obj   给定字符串
     * @param field 要设置的域对象
     * @param e     要给设置的对象
     * @param <E>   元素范型
     * @throws IllegalAccessException 不合法访问异常
     */
    private static <E> void configReasonableString(Object obj, Field field, E e) throws IllegalAccessException {
        int substringThreshold = 45;
        if (!(obj instanceof String)) {
            throw new IllegalArgumentException("参数obj应该为String类型，而给定不是");
        } else if (((String) obj).length() <= substringThreshold) {
            field.set(e, obj);
            return;
        }
        String str = (String) obj;
        int indexOf = str.indexOf("。");
        int minIndex = 40, maxIndex = 50;
        if (indexOf > minIndex && indexOf <= maxIndex) {
            field.set(e, str.substring(0, indexOf));
        } else {
            field.set(e, str.substring(0, 45));
        }
    }

    private static final OpenIdVo OPEN_ID_VO = (OpenIdVo) SpringUtil.getBean("openIdVo");

    public static String getAccessToken() {
        String url = WeChatEnum.SUB_MESSAGE_ACCESS_TOKEN_URL_GET.getValue() + "&appid=" + OPEN_ID_VO.getAppid() + "&secret=" + OPEN_ID_VO.getSecret();
        String subInternetMessage = HttpUtil.get(url);
        return JsonUtil.parseJson(subInternetMessage).findPath("access_token").toString();
    }

    /**
     * @param parentId    被通知文章内容的id
     * @param showContent 服务通知展示的内容
     * @param title       服务通知展示的标题
     * @param nickname    服务通知展示的发送者昵称
     * @param touser      接受者open id
     * @param templateId  模版ID
     * @param page        服务通知进入的小程序页面
     * @param version     小程序版本
     */
    public static void subMessage(String parentId
            , String showContent, String title, String nickname
            , String touser
            , WeChatEnum templateId, WeChatEnum page, WeChatEnum version) {
        String accessToken = GeneralDataProcessUtil.getAccessToken();
        SubMessageDataVo data = new SubMessageDataVo(
                new SubMessageValueVo(showContent.length() >= 20 ? showContent.substring(0, 20) : showContent)
                , new SubMessageValueVo(nickname)
                , new SubMessageValueVo(LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                , new SubMessageValueVo(title.length() >= 20 ? title.substring(0, 20) : title));
        JSONObject objectNode = new JSONObject();
        objectNode.put("touser", touser);
        objectNode.put("template_id", templateId.getValue());
        objectNode.put("page", page.getValue() + "?id=" + parentId);
        objectNode.put("data", data);
        objectNode.put("miniprogram_state", version.getValue());
        String post = HttpUtil.post(WeChatEnum.SUB_MESSAGE_SEND_URL_POST.getValue() + "?access_token=" + accessToken.replace("\"", ""), objectNode.toString());

    }

//    void updateRedis(){
//        long start = System.currentTimeMillis();
//        List<String> allOpenId = userMapper.selectAllOpenId();
//        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
//        allOpenId.forEach(openId -> {
//            del(openId);
//            List<Letter> letterList = letterMapper.selectAllLetterByOpenId(openId, 1);
//            letterList.forEach(letter -> {
//                update(openId,"letter",sdf.format(letter.getReleaseTime()));
//            });
//            List<Diarys> diarysList = diaryMapper.selectAllPublicDiary("1");
//            diarysList.forEach(diarys -> {
//                update(openId,"diary",sdf.format(diarys.getDate()));
//            });
//            List<SpittingGrooves> spittingGrooves = spittingGroovesMapper.selectAllSpit();
//            spittingGrooves.forEach(spittingGrooves1 -> {
//                update(openId,"spit",sdf.format(spittingGrooves1.getDate()));
//            });
//
//        });
//        System.out.println((System.currentTimeMillis() - start) + "ms");
//    }
//    void update(String openId,String eventType,String time){
//        String timelineKey = "user:" + openId + ":timeline";
//        @SuppressWarnings("unchecked") LinkedList<TimelineVo> timelineVoLinkedList = (LinkedList<TimelineVo>) redisUtil.get(timelineKey);
//        TimelineVo timelineVo = new TimelineVo(openId, eventType, time);
//        if (timelineVoLinkedList == null) {
//            LinkedList<TimelineVo> timelineVos = new LinkedList<>();
//            timelineVos.addFirst(timelineVo);
//            redisUtil.set(timelineKey, timelineVos);
//        } else {
//            timelineVoLinkedList.addFirst(timelineVo);
//            redisUtil.set(timelineKey, timelineVoLinkedList);
//        }
//    }
//    void del(String openId){
//        String timelineKey = "user:" + openId + ":timeline";
//        redisUtil.del(timelineKey);
//    }
}
