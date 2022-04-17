package com.yundingxi.common.util.strategy;

import cn.hutool.http.HttpUtil;
import com.yundingxi.web.util.GeneralDataProcessUtil;
import com.yundingxi.tell.bean.dto.WeChatEnum;
import com.yundingxi.tell.bean.vo.submessage.SubMessageCommentDataVo;
import com.yundingxi.tell.bean.vo.submessage.SubMessageParam;
import com.yundingxi.tell.bean.vo.submessage.SubMessageReplyVo;
import com.yundingxi.tell.bean.vo.submessage.SubMessageValueVo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName SubMessageStrategyContext
 * @Author rayss
 * @Datetime 2021/5/29 9:08 上午
 */

public class SubMessageStrategyContext {

    /**
     * 根据模版ID选择使用哪中方式执行订阅消息
     *
     * @param templateId 模版ID
     * @return 订阅消息策略模式接口
     */
    public static SubMessageStrategy getSubMessageStrategy(WeChatEnum templateId) {
        Map<String, String> allClazz = SubMessageTemplateEnum.getAllClazz();
        String clazz = allClazz.get(templateId.getValue());
        SubMessageStrategy strategy = null;
        try {
            strategy = (SubMessageStrategy) Class.forName(clazz).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return strategy;
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
