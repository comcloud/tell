package com.yundingxi.tell.util;

import com.yundingxi.tell.bean.dto.IndexLetterDto;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.vo.DiaryReturnVo;
import com.yundingxi.tell.bean.vo.SpittingGroovesVo;
import com.yundingxi.tell.mapper.UserMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    public static List<IndexLetterDto> configLetterDataFromList(List<Letter> letterList, String openId) {
        List<IndexLetterDto> letterDtoList = new ArrayList<>();
        letterList.forEach(letter -> letterDtoList.add(new IndexLetterDto(letter.getContent().length() > 50 ? letter.getContent().substring(0, 50) : letter.getContent(), letter.getOpenId(), letter.getId(), letter.getPenName(), letter.getStampUrl(), USER_MAPPER.selectPenNameByOpenId(openId), letter.getReleaseTime())));
        return letterDtoList;
    }

    public static List<DiaryReturnVo> configDiaryDataFromList(List<Diarys> diaryList) {
        List<DiaryReturnVo> diaryDtoList = new ArrayList<>();
        diaryList.forEach(diary -> diaryDtoList.add(new DiaryReturnVo(diary.getId(), diary.getContent().length() > 60 ? diary.getContent().substring(0, 60) : diary.getContent(), diary.getPenName(), diary.getWeather(), diary.getOpenId(), diary.getState(), diary.getNumber(), diary.getDate())));
        return diaryDtoList;
    }

    public static List<SpittingGroovesVo> configSpitDataFromList(List<SpittingGrooves> spittingGroovesList) {
        List<SpittingGroovesVo> spittingGroovesVoList = new ArrayList<>();
        spittingGroovesList.forEach(spit -> spittingGroovesVoList.add(new SpittingGroovesVo(spit.getId(), spit.getNumber(), spit.getTitle(), spit.getAvatarUrl(), spit.getPenName())));
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
                        configReasonableString(obj,field,r);
                        continue;
                    } else if ("content".equals(declaredField.getName()) && paramType == Letter.class) {
                        configReasonableString(obj,field,r);
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

    private static <R> void configReasonableString(Object obj, Field field, R r) throws IllegalAccessException {
        String str = (String) obj;
        int indexOf = str.indexOf("。");
        if (indexOf > 40 && indexOf <= 50) {
            field.set(r, str.substring(0, indexOf));
        } else {
            field.set(r, str.substring(0, 45));
        }
    }
}
