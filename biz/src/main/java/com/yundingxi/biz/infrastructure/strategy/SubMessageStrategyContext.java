package com.yundingxi.biz.infrastructure.strategy;

import cn.hutool.http.HttpUtil;
import com.yundingxi.biz.util.GeneralDataProcessUtil;
import com.yundingxi.common.model.enums.WeChatEnum;
import com.yundingxi.model.vo.submessage.SubMessageCommentDataVo;
import com.yundingxi.model.vo.submessage.SubMessageParam;
import com.yundingxi.model.vo.submessage.SubMessageReplyVo;
import com.yundingxi.model.vo.submessage.SubMessageValueVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @version v1.0
 * @ClassName SubMessageStrategyContext
 * @Author rayss
 * @Datetime 2021/5/29 9:08 上午
 */
@Component
public class SubMessageStrategyContext implements InitializingBean {

    /**
     * 策略缓存
     */
    private static final Map<String, SubMessageStrategy> STRATEGY_CACHE = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        STRATEGY_CACHE.putAll(SubMessageTemplateEnum.getAllClazz().entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            try {
                                return (SubMessageStrategy) Class.forName(entry.getValue()).newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                                return SubMessageStrategy.nullSubMessageStrategy();
                            }
                        })));
    }

    /**
     * 根据模版ID选择使用哪中方式执行订阅消息
     *
     * @param templateId 模版ID
     * @return 订阅消息策略模式接口
     */
    public static SubMessageStrategy getSubMessageStrategy(WeChatEnum templateId) {
        if (Objects.isNull(templateId)) {
            return SubMessageStrategy.nullSubMessageStrategy();
        }
        return STRATEGY_CACHE.get(templateId.getValue());
    }


    /**
     * 回信订阅消息策略实现类
     */
    @SuppressWarnings("unused")
    public static class ReplySubMessageStrategy implements SubMessageStrategy {


        @Override
        public void processSubMessage(SubMessageParam param, String... reserveParam) {
            String accessToken = GeneralDataProcessUtil.getAccessToken();
            Object data = new SubMessageReplyVo(
                    new SubMessageValueVo(param.getNickname())
                    , new SubMessageValueVo(LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    , new SubMessageValueVo(param.getShowContent().length() > 20 ? param.getShowContent().substring(0, 20) : param.getShowContent()));
            String body = GeneralDataProcessUtil.packageSubMessageJson(data, param, reserveParam[0]);
            String post = HttpUtil.post(WeChatEnum.SUB_MESSAGE_SEND_URL_POST.getValue() + "?access_token=" + accessToken.replace("\"", ""), body);
            System.out.println(param.getNickname() + ":::" + param.getTouser() + post);
        }
    }

    /**
     * 评论订阅消息策略实现类
     */
    @SuppressWarnings("unused")
    public static class CommentSubMessageStrategy implements SubMessageStrategy {

        @Override
        public void processSubMessage(SubMessageParam param, String... reserveParam) {
            String accessToken = GeneralDataProcessUtil.getAccessToken();
            Object data = new SubMessageCommentDataVo(
                    new SubMessageValueVo(param.getShowContent().length() >= 20 ? param.getShowContent().substring(0, 20) : param.getShowContent())
                    , new SubMessageValueVo(param.getNickname())
                    , new SubMessageValueVo(LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    , new SubMessageValueVo(param.getTitle().length() >= 20 ? param.getTitle().substring(0, 20) : param.getTitle()));
            String body = GeneralDataProcessUtil.packageSubMessageJson(data, param, "");
            String post = HttpUtil.post(WeChatEnum.SUB_MESSAGE_SEND_URL_POST.getValue() + "?access_token=" + accessToken.replace("\"", ""), body);
            System.out.println(param.getNickname() + ":::" + param.getTouser() + post);
        }
    }
}
