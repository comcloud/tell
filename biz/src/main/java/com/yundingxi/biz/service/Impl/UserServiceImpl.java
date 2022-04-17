package com.yundingxi.biz.service.Impl;

import cn.hutool.http.HttpUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.biz.util.GeneralDataProcessUtil;
import com.yundingxi.common.model.enums.redis.RedisEnums;
import com.yundingxi.common.redis.RedisUtil;
import com.yundingxi.common.util.*;
import com.yundingxi.biz.infrastructure.pipeline.context.UserDataAnalysisContext;
import com.yundingxi.biz.infrastructure.pipeline.executor.PipelineExecutor;
import com.yundingxi.dao.mapper.*;
import com.yundingxi.dao.model.*;
import com.yundingxi.model.vo.*;
import com.yundingxi.model.dto.*;
import com.yundingxi.biz.service.SpittingGroovesService;
import com.yundingxi.biz.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author hds
 * <p>项目名称:
 * <p>文件名称:
 * <p>描述:
 * @date 2021/3/26-20:37
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final RedisUtil redisUtil;
    private final OpenIdVo openIdVo;
    private final LetterMapper letterMapper;
    private final DiaryMapper diaryMapper;
    private final SpittingGroovesMapper spittingGroovesMapper;
    private final StampMapper stampMapper;


    @Autowired
    public UserServiceImpl(UserMapper userMapper, RedisUtil redisUtil, OpenIdVo openIdVo, LetterMapper letterMapper, DiaryMapper diaryMapper, SpittingGroovesMapper spittingGroovesMapper, StampMapper stampMapper) {
        this.userMapper = userMapper;
        this.redisUtil = redisUtil;
        this.openIdVo = openIdVo;
        this.letterMapper = letterMapper;
        this.diaryMapper = diaryMapper;
        this.spittingGroovesMapper = spittingGroovesMapper;
        this.stampMapper = stampMapper;
    }

    @Override
    public Result<String> insertUser(User user) {

        try {
            user.setRegistrationTime(new Date());
            Integer integer = userMapper.insertUser(user);
            if (integer > 0) {
                //给新用户插入默认邮票
                List<UserStamp> userStampList = getUserStamps(user.getOpenId());
                userStampList.forEach(stampMapper::insertSingleNewUserStamp);

                return ResultGenerator.genSuccessResult("用户注册成功!!!!!");
            } else {
                return ResultGenerator.genFailResult("注册失败！！！");
            }
        } catch (DuplicateKeyException e) {
            return ResultGenerator.genSuccessResult("已经注册过了!!!!!");
        }
    }

    /**
     * 获取用户默认邮票列表
     */
    private List<UserStamp> getUserStamps(String openId) {
        //每个人赋予默认邮票
        List<Stamp> baseStamp = stampMapper.selectBaseStamp();
        List<UserStamp> userStampList = new ArrayList<>();
        baseStamp.forEach(stamp -> userStampList.add(new UserStamp(UUID.randomUUID().toString(), stamp.getId(), openId, "1", new Date(), 1)));
        return userStampList;
    }

    @Override
    public String getKey(String jsCode) {
        String baseUrl = openIdVo.getBaseUrl() + jsCode;
        String appid = openIdVo.getAppid();
        String secret = openIdVo.getSecret();
        String grantType = openIdVo.getGrantType();
        return HttpUtil.get(baseUrl + "&appid=" + appid + "&secret=" + secret + "&grant_type=" + grantType);
    }

    @Override
    public Result<Object> getAllUserCommentVo(String openId, Integer pageNum) {
        int size = 5;
        int size1 = redisUtil.lGet("comm:" + openId + ":info", 0, -1).size();
        int pageNumMax = size1 / size;
        int a = size1 % size;
        pageNumMax = a == 0 ? pageNumMax : pageNumMax + 1;
        if (pageNum > pageNumMax && size1 != 0) {
            return ResultGenerator.genSuccessResult();
        }
        List<Object> objects = redisUtil.lGet("comm:" + openId + ":info", (pageNum - 1) * size, pageNum * size - 1);
        redisUtil.del("comm:" + openId + ":count");
        if (objects.isEmpty()) {
            List<UserCommentVo> userCommentVos = userMapper.getUserCommentVos(openId);
            for (UserCommentVo userCommentVo : userCommentVos) {
                redisUtil.rSet("comm:" + openId + ":info", userCommentVo);
            }
            List<Object> objects1 = redisUtil.lGet("comm:" + openId + ":info", (pageNum - 1) * size, pageNum * size - 1);
            return ResultGenerator.genSuccessResult(objects1);
        }
        return ResultGenerator.genSuccessResult(objects);
    }

    @Override
    public Result<Object> getCommNum(String openId) {
        Object o = redisUtil.get("comm:" + openId + ":count");
        return ResultGenerator.genSuccessResult(o);
    }

    @Override
    public Result<String> updateUser(User entity) {
        if (userMapper.updateUser(entity) > 0) {
            return ResultGenerator.genSuccessResult("user 用户 信息 修改  成功!!!");
        }
        return ResultGenerator.genFailResult("更新失败!!!!!");
    }

    @Override
    public Result<String> updateOutDate(String openId) {
        if (userMapper.updateOutDate(openId) > 0) {
            return ResultGenerator.genSuccessResult("user 用户 退出  成功!!!,退出时间已经记录");
        }
        return ResultGenerator.genFailResult("最后登录时间记录失败!!!!!");
    }

    @Override
    public Result<ProfileVo> getProfile(String openId) {
        int numOfLetter = letterMapper.selectNumberOfLetterByOpenIdNonState(openId, 4);
        int numOfDiary = diaryMapper.selectNumberOfDiaryByOpenIdNonState(openId, "4");
        int numOfSpit = spittingGroovesMapper.selectNumberOfLetSpitByOpenIdNonState(openId, "4");
        int numOfReply = letterMapper.selectNumberOfReply(openId);
        User user = userMapper.selectNameAndUrlByOpenId(openId);
        if (user == null) {
            return ResultGenerator.genFailResult(new ProfileVo("用户不存在", null, null, null));
        }

        List<ProfileNumVo> numVos = new ArrayList<>();
        numVos.add(new ProfileNumVo("解忧", numOfLetter));
        numVos.add(new ProfileNumVo("日记", numOfDiary));
        numVos.add(new ProfileNumVo("吐槽", numOfSpit));

        ProfileVo profileVo = new ProfileVo(user.getPenName(), user.getAvatarUrl(), numVos, numOfReply);
        return ResultGenerator.genSuccessResult(profileVo);
    }

    /**
     * 计算给定时间戳之前四周的用户数据分析记录
     * 返回结果中是一个ModelUtil，第一个值List<List<String>>表示用户的历史发布内容
     * 第二个是用户的数据分析，使用Map<String,List<ProfileNumVo>>>存储
     * @param openId           open Id
     * @param currentTimeStamp 当前时间戳
     * @return 用户分析数据结果
     */
    @SneakyThrows
    @Override
    public Result<ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>> getDataAnalysis(String openId, Long currentTimeStamp) {
        if (openId == null || "".equals(openId)) {
            return ResultGenerator.genSuccessResult(new ModelUtil<>());
        }
        @SuppressWarnings("unchecked") ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>> model
                = (ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>) redisUtil.get(RedisEnums.USER_DATA_ANALYSIS_MODEL.getRedisKey() + "_" + openId + ":data");
        if (model == null) {
            //此时缓存中没有数据，所以需要进行重新计算，这里计算的方式使用管道模式，一种Pipeline设计模式
            //将每个需要计算的内容进行设置为单个的value，然后组成一个管道使用
            PipelineExecutor<ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>> executor = new PipelineExecutor<>();
            UserDataAnalysisContext context = UserDataAnalysisContext.builder().openId(openId).currentTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTimeStamp))).build();
            executor.acceptSync(context);
            ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>> result = context.getResult();
            //这个位置为设计漏洞，也就是将变量单独存储，然后执行完成之后再自己赋值
            result.setLastValue(context.getAnalysis());
            redisUtil.set(RedisEnums.USER_DATA_ANALYSIS_MODEL.getRedisKey() + "_" + openId + ":data", result, TimeUnit.DAYS.toSeconds(30));
            return ResultGenerator.genSuccessResult(result);
        }
        return ResultGenerator.genSuccessResult(model);
    }

    @Override
    public Result<Integer> isTextLegal(String textContent) {
        Integer conclusionType = NaturalLanguageUtil.getTextLegalType(textContent);
        return ResultGenerator.genSuccessResult(conclusionType);
    }

    @Override
    public Result<HistoryDataVo> getDataOfHistory(String openId) {
        HistoryDataVo data = new HistoryDataVo();
        List<Letter> letterList = letterMapper.selectAllLetterByOpenIdNonState(openId, 4);
        List<Diarys> diaryList = diaryMapper.selectAllDiaryByOpenIdAndNonState(openId, "4");
        List<SpittingGroovesVo> spittingGroovesList = spittingGroovesMapper.selectAllSpitForSelfNonState(openId, "4");

        data.setLetterList(GeneralDataProcessUtil.configDataFromList(letterList, Letter.class, LetterVo.class));
        data.setDiaryList(GeneralDataProcessUtil.configDataFromList(diaryList, Diarys.class, DiaryReturnVo.class));
        data.setSpittingGroovesList(resolveTitle(spittingGroovesList));
        return ResultGenerator.genSuccessResult(data);
    }

    /**
     * 返回一个吐槽对象vo列表，为了解决标题问题
     * @param voList 吐槽对象集合
     * @return 吐槽对象vo
     */
    private List<SpittingGroovesVo> resolveTitle(List<SpittingGroovesVo> voList) {
        Random random = new Random();
        voList.forEach(vo -> {
            String content = vo.getTitle();
            vo.setTitle(content.length() > 30 ? content.substring(0, 20 + (random.nextInt(10))) : content);
        });
        return voList;
    }

    @Override
    public Result<Object> getOfficialMsg(String openId) {
        Object o = redisUtil.leftPop(RedisEnums.USER_OFFICIAL_MSG.getRedisKey() + ":" + openId);
        return ResultGenerator.genSuccessResult(o);
    }

    @Override
    public Result<PageInfo<TimelineVo>> getTimelineData(String openId, Integer pageNum) {
        @SuppressWarnings("unchecked") List<TimelineVo> timelineVoList = (List<TimelineVo>) redisUtil.get("user:" + openId + ":timeline");
        PageHelper.startPage(pageNum, 15);
        return ResultGenerator.genSuccessResult(new PageInfo<>(timelineVoList == null ? new LinkedList<>() : timelineVoList));
    }

    @Override
    public Result<String> saveQuestionnaire(QuestionnaireDto questionnaireDto) {
        Questionnaire questionnaire = new Questionnaire(questionnaireDto.getOpenId(), questionnaireDto.getIsIllegal(), questionnaireDto.getIsHelp(), questionnaireDto.getInterestScore(), questionnaireDto.getPageScore(), questionnaireDto.getOtherSpeech(), new Date());
        int result = userMapper.insertQuestionnaire(questionnaire);
        return result == 1 ? ResultGenerator.genSuccessResult("保存成功") : ResultGenerator.genFailResult("保存失败");
    }

    @Override
    public List<String> selectAllOpenId() {
        return userMapper.selectAllOpenId();
    }

}
