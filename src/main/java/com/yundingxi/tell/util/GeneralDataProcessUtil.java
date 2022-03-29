package com.yundingxi.tell.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yundingxi.tell.bean.dto.IndexLetterDto;
import com.yundingxi.tell.bean.dto.LetterReplyDto;
import com.yundingxi.tell.bean.dto.WeChatEnum;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.*;
import com.yundingxi.tell.bean.vo.submessage.SubMessageCommentDataVo;
import com.yundingxi.tell.bean.vo.submessage.SubMessageParam;
import com.yundingxi.tell.bean.vo.submessage.SubMessageReplyVo;
import com.yundingxi.tell.bean.vo.submessage.SubMessageValueVo;
import com.yundingxi.tell.mapper.UserMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
     * 订阅消息
     */
    public static String packageSubMessageJson(Object data, SubMessageParam param, String replyId) {
        JSONObject objectNode = new JSONObject();
        if (param.getObj() instanceof LetterReplyDto) {
            LetterReplyDto replyDto = (LetterReplyDto) param.getObj();
            objectNode.put("page", param.getPage().getValue()
                    + "?letterId=" + replyDto.getLetterId()
                    + "&senderOpenId=" + replyDto.getSender()
                    + "&recipientPenName" + USER_MAPPER.selectPenNameByOpenId(replyDto.getRecipient())
                    + "&senderPenName=" + replyDto.getSenderPenName()
                    + "&replyId=" + replyId);
        } else {
            objectNode.put("page", param.getPage().getValue() + "?id=" + param.getParentId());
        }
        objectNode.put("touser", param.getTouser());
        objectNode.put("template_id", param.getTemplateId().getValue());
        objectNode.put("data", data);
        objectNode.put("miniprogram_state", param.getVersion().getValue());
        return objectNode.toString();
    }

    /**
     * @param analysisContentList 要被分析的内容集合
     * @return 分析的单个结果
     */
    public static List<ProfileNumVo> singleAnalysis(List<String> analysisContentList) {
        List<ProfileNumVo> universalList = new ArrayList<>();
        universalList.add(new ProfileNumVo("愤怒", 0));
        universalList.add(new ProfileNumVo("低落", 0));
        universalList.add(new ProfileNumVo("温和", 0));
        universalList.add(new ProfileNumVo("喜悦", 0));
        int[] gradeArray = new int[4];
        analysisContentList.forEach(content -> {
            Integer analysisResult = NaturalLanguageUtil.emotionAnalysis(content);
            gradeArray[analysisResult] = gradeArray[analysisResult] + 1;
        });
        for (int i = 0; i < gradeArray.length; i++) {
            universalList.get(i).setValue(gradeArray[i]);
        }
        return universalList;
    }
}