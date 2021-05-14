package com.yundingxi.tell.service.Impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yundingxi.tell.bean.entity.Stamp;
import com.yundingxi.tell.bean.vo.StampVo;
import com.yundingxi.tell.mapper.StampMapper;
import com.yundingxi.tell.service.StampService;
import com.yundingxi.tell.util.Result;
import com.yundingxi.tell.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hds
 * @since 2021-05-10
 */
@Service
public class StampServiceImpl implements StampService {
    @Autowired
    private StampMapper stampMapper;
    @Override

    public Result<List<StampVo>> getAllStamp(String openId, Integer pageNum) {
//        String orderBy = "us.obtain_time desc";
//        PageHelper.startPage(pageNum,10,orderBy);
        List<StampVo> stampVoList = stampMapper.haveListMeAll(openId);
        stampVoList.forEach(stampVo -> stampVo.setLock(false));
//        PageInfo<StampVo> pageInfo = new PageInfo<>(stampVo);
//        Map<String, Object> stringObjectHashMap = new HashMap<>(2);
//        stringObjectHashMap.put("have",pageInfo);
//        stringObjectHashMap.put("notHave",stampMapper.notHaveListMeAll(openId));
        return ResultGenerator.genSuccessResult(stampVoList);
    }

    @Override
    public Result<List<StampVo>> getAllStampForAlbum(String openId) {
        List<StampVo> stampVos = new ArrayList<>();
        List<Stamp> stampList = stampMapper.selectAllStamp();
        stampList.forEach(stamp -> stampVos.add(new StampVo(stamp.getStampUrl(),stamp.getStampName(),stamp.getStampSeries(),stamp.getStampNumber(),stamp.getStampDesc(),stamp.getStampEdition(),null,true)));
        List<StampVo> haveStampVoList = stampMapper.haveListMeAll(openId);
        stampVos.forEach(stampVo -> {
            for (StampVo vo : haveStampVoList) {
                if(vo.getStampNumber().equals(stampVo.getStampNumber())){
                    stampVo.setLock(false);
                    break;
                }
            }
        });
        return ResultGenerator.genSuccessResult(stampVos);
    }
}
