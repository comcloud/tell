package com.yundingxi.tell.service.Impl;

import cn.hutool.http.HttpUtil;
import com.yundingxi.tell.bean.entity.Diarys;
import com.yundingxi.tell.bean.entity.Letter;
import com.yundingxi.tell.bean.entity.SpittingGrooves;
import com.yundingxi.tell.bean.entity.User;
import com.yundingxi.tell.bean.vo.ProfileNumVo;
import com.yundingxi.tell.bean.vo.ProfileVo;
import com.yundingxi.tell.bean.vo.OpenIdVo;
import com.yundingxi.tell.bean.vo.UserCommentVo;
import com.yundingxi.tell.common.enums.RedisEnums;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.mapper.DiaryMapper;
import com.yundingxi.tell.mapper.LetterMapper;
import com.yundingxi.tell.mapper.SpittingGroovesMapper;
import com.yundingxi.tell.mapper.UserMapper;
import com.yundingxi.tell.service.SpittingGroovesService;
import com.yundingxi.tell.service.UserService;
import com.yundingxi.tell.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.SneakyThrows;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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


    @Autowired
    public UserServiceImpl(UserMapper userMapper, RedisUtil redisUtil, OpenIdVo openIdVo, LetterMapper letterMapper, DiaryMapper diaryMapper, SpittingGroovesMapper spittingGroovesMapper) {
        this.userMapper = userMapper;
        this.redisUtil = redisUtil;
        this.openIdVo = openIdVo;
        this.letterMapper = letterMapper;
        this.diaryMapper = diaryMapper;
        this.spittingGroovesMapper = spittingGroovesMapper;
    }

    @Override
    public Result<String> insertUser(User user) {

        try {
            user.setRegistrationTime(new Date());
            Integer integer = userMapper.insertUser(user);
            if (integer > 0) {
                return ResultGenerator.genSuccessResult("用户注册成功!!!!!");
            } else {
                return ResultGenerator.genFailResult("注册失败！！！");
            }
        } catch (DuplicateKeyException e) {
            return ResultGenerator.genSuccessResult("已经注册过了!!!!!");
        }
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
        System.out.println(entity);
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
        int numOfLetter = userMapper.selectNumberOfLetterByOpenId(openId);
        int numOfDiary = userMapper.selectNumberOfDiaryByOpenId(openId);
        int numOfSpit = userMapper.selectNumberOfLetSpitByOpenId(openId);
        User user = userMapper.selectNameAndUrlByOpenId(openId);
        if(user == null){
            return ResultGenerator.genFailResult(new ProfileVo("用户不存在",null,null));
        }

        List<ProfileNumVo> numVos = new ArrayList<>();
        numVos.add(new ProfileNumVo("解忧", numOfLetter));
        numVos.add(new ProfileNumVo("日记", numOfDiary));
        numVos.add(new ProfileNumVo("吐槽", numOfSpit));

        ProfileVo profileVo = new ProfileVo(user.getPenName(), user.getAvatarUrl(), numVos);
        return ResultGenerator.genSuccessResult(profileVo);
    }

    @SneakyThrows
    @Override
    public Result<ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>> getDataAnalysis(String openId,Long currentTimeStamp) {
        @SuppressWarnings("unchecked") ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>> model
                = (ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>>) redisUtil.get(RedisEnums.USER_DATA_ANALYSIS_MODEL.getRedisKey() + "_" + openId + "_" + currentTimeStamp + ":data");
        if (model == null) {
            ModelUtil<List<List<String>>, Map<String, List<ProfileNumVo>>> result = new ModelUtil<>();
            Date currentDate = new Date(currentTimeStamp);
            CompletableFuture
                    .runAsync(() -> result.setFirstValue(configureReview(openId,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate))))
                    .thenRunAsync(() -> result.setLastValue(configureDataAnalysis(openId,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate))))
                    .get();
            redisUtil.set(RedisEnums.USER_DATA_ANALYSIS_MODEL.getRedisKey() + "_" + openId + "_" + currentTimeStamp +  ":data", result, TimeUnit.DAYS.toSeconds(30));
            return ResultGenerator.genSuccessResult(result);
        } else {
            return ResultGenerator.genSuccessResult(model);
        }
    }

    @Override
    public Result<Integer> isTextLegal(String textContent) {
        Integer conclusionType = NaturalLanguageUtil.getTextLegalType(textContent);
        return ResultGenerator.genSuccessResult(conclusionType);
    }

    /**
     * 配置历史发布
     */
    private List<List<String>> configureReview(String openId,String currentTime) {
        List<List<String>> review = new ArrayList<>();
        review.add(Arrays.asList("发布数量", "解忧", "日记", "吐槽"));
        for (int i = 0; i < 4; i++) {
            review.add(Arrays.asList("第" + (i + 1) + "周"
                    , letterMapper.selectWeeklyQuantityThroughOpenId(openId, currentTime,"letter", i, 7) + ""
                    , letterMapper.selectWeeklyQuantityThroughOpenId(openId, currentTime,"diarys", i, 7) + ""
                    , letterMapper.selectWeeklyQuantityThroughOpenId(openId, currentTime,"spitting_grooves", i, 7) + "")
            );
        }
        return review;
    }

    /**
     * 配置数据分析内容
     */
    @SneakyThrows
    private Map<String, List<ProfileNumVo>> configureDataAnalysis(String openId,String currentTime) {
        Map<String, List<ProfileNumVo>> analysis = new HashMap<>(3);

        List<String> letterContentList = letterMapper.selectAllLetterContentByOpenId(openId,currentTime);
        List<String> diaryContentList = diaryMapper.selectAllDiaryContentByOpenId(openId,currentTime);
        List<String> spittingGroovesContentList = spittingGroovesMapper.selectAllSpitContentByOpenId(openId,currentTime);
        CompletableFuture
                .runAsync(() -> analysis.put("letter", singleAnalysis(letterContentList)))
                .thenRunAsync(() -> analysis.put("diary", singleAnalysis(diaryContentList)))
                .thenRunAsync(() -> analysis.put("spit_groove", singleAnalysis(spittingGroovesContentList)))
                .get();
        return analysis;
    }

    /**
     * @param analysisContentList 要被分析的内容集合
     * @return 分析的单个结果
     */
    private List<ProfileNumVo> singleAnalysis(List<String> analysisContentList) {
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
