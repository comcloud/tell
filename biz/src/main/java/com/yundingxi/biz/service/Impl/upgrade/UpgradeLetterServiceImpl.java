package com.yundingxi.biz.service.Impl.upgrade;


import com.yundingxi.common.redis.RedisUtil;
import com.yundingxi.common.util.GeneralDataProcessUtil;
import com.yundingxi.dao.mapper.LetterMapper;
import com.yundingxi.dao.mapper.ReplyMapper;
import com.yundingxi.dao.mapper.UserMapper;
import com.yundingxi.dao.model.Letter;
import com.yundingxi.model.dto.letter.IndexLetterDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @version v1.0
 * @ClassName UpgradeLetterServiceImpl
 * @Author rayss
 * @Datetime 2021/7/27 4:13 下午
 */
@Service
@Slf4j
public class UpgradeLetterServiceImpl extends AbstractLetterServiceImpl {

    public UpgradeLetterServiceImpl(LetterMapper letterMapper, RedisUtil redisUtil, ReplyMapper replyMapper, UserMapper userMapper) {
        super(letterMapper, redisUtil, replyMapper, userMapper);
    }

    /**
     * 这里需要做出优化
     * 1.优先根据标签获取信件，如何判断
     * - "letter:" + openId + ":letter_info"这个缓存中存储每个用户的喜爱(此用户回信的信标签)的对应标签数量
     * 获取两则占比最大的，然后这个表示最有可能推荐的，查询数据库时候按照标签进行分组，获取数据库中这个标签组中两片信件
     * - 如果上述条件获取的是两封，然后这个是获取其他不相干类型的一封，以来扩展
     * - 都要保证时间优先，也就是优先把最新的信件推送出去
     * - 随机获取，不可以排着数据库获取，而是随机获取，当然每日不可以重复
     * <p>
     * 1.从缓存中获取已经读取到的位置数据，用来判断数据库中的数据是否是最新的
     * <p>
     * <p>
     * - 设计一个算法计算阈值
     * - 添加权重比值判断到获取随机数中，在计算随机数时候将此因素考虑进去
     *
     * @param openId 用户 open id
     * @return 获取三封信件
     */
    @Override
    protected List<IndexLetterDto> customGetLettersByOpenId(String openId, int totalNumber) {
        //此时需要从数据库获取内容,随机三个数字获取数据库中最新的十条数据中的位置
        int gainLetterNumber = totalNumber == 1 || totalNumber == 2 ? totalNumber : 3;
        List<IndexLetterDto> indexLetterDtoList = new ArrayList<>(3);
        //用来解决生成随机数重复问题，以防出现相同的信件，当然如果数据库的数据比较少，进行randomInt+1之后还是会有重复
        int[] randomIntArray = getDifferentArray(totalNumber, gainLetterNumber);
        for (int i = 0; i < gainLetterNumber; i++) {
            int randomInt = randomIntArray[i];
            Letter letter = letterMapper.selectRandomLatestLetter(openId, randomInt % totalNumber, 1, 1);
            GeneralDataProcessUtil.configLetterDataFromSingleObject(letter, openId, indexLetterDtoList);
        }
        return indexLetterDtoList;
    }


    /**
     * 获取length个不同数字的数字
     *
     * @param surplusThreshold 求余的阈值
     * @param length           要获取的长度
     * @return 不同数字数组
     */
    private int[] getDifferentArray(int surplusThreshold, int length) {
        Random random = new Random();
        int[] differentArray = new int[length];
        for (int i = 0; i < differentArray.length; i++) {
            differentArray[i] = spinRandomNumberToNonExist(random.nextInt(surplusThreshold), surplusThreshold, i, differentArray);
        }
        return differentArray;
    }

    /**
     * 自旋直到产生没有出现过的数字
     *
     * @param randomNumber  随机数字
     * @param spinThreshold 自旋阈值，表示每次对哪个数字进行取余
     * @param length        已经有的数字长度
     * @param alreadyNumber 已经出现的数字
     * @return 自旋数字结果
     */
    private int spinRandomNumberToNonExist(int randomNumber, int spinThreshold, int length, int... alreadyNumber) {
        for (; ; ) {
            //成功标记，表示着是否已经不存在重复数字
            boolean successFlag = true;
            for (int i = 0; i < length; i++) {
                if (alreadyNumber[i] == randomNumber) {
                    randomNumber = (randomNumber + 1) % spinThreshold;
                    successFlag = false;
                }
            }
            if (successFlag) {
                return randomNumber;
            }
        }
    }


}
