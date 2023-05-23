package com.memory.yun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memory.yun.interceptor.LoginInterceptor;
import com.memory.yun.model.LabelDO;
import com.memory.yun.mapper.LabelMapper;
import com.memory.yun.model.LoginUser;
import com.memory.yun.service.LabelService;
import com.memory.yun.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    LabelMapper labelMapper;

    @Autowired
    RecordService recordService;

    @Override
    public int addLabel(String color, String name, Integer team1) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        LabelDO labelDO = new LabelDO();
        labelDO.setColor(color);
        labelDO.setName(name);
        labelDO.setTeam1(team1);
        labelDO.setUserId(user.getId());
        LabelDO labelOld = labelMapper.selectOne(new QueryWrapper<LabelDO>().eq("user_id",user.getId()).eq("team1",team1).eq("name",name));
        if (labelOld == null) {
            return labelMapper.insert(labelDO);
        }
        return -1;
    }

    @Override
    public int inerAddLabel(LabelDO labelDO) {
        return labelMapper.insert(labelDO);
    }

    @Override
    public int update(LabelDO labelDO) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        if (labelDO.getUserId()==user.getId()){
            return labelMapper.updateById(labelDO);
        }
        return -1;
    }

    @Override
    @Transactional
    public int delLabel(long labelId) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        recordService.delByLabel(labelId,user.getId());
        return labelMapper.delete(new QueryWrapper<LabelDO>().eq("id",labelId).eq("user_id",user.getId()));
    }

    @Override
    public int delByteam(long teamId) {
        long userId = LoginInterceptor.threadLocal.get().getId();

        return labelMapper.delete(new QueryWrapper<LabelDO>().eq("team1",teamId).eq("user_id",userId));
    }

    @Override
    public List<LabelDO> find(long labelId) {
        long userId=LoginInterceptor.threadLocal.get().getId();
        return labelMapper.selectList(new QueryWrapper<LabelDO>().eq("team1",labelId).eq("user_id",userId));
    }

    @Override
    public List<LabelDO> findAll() {
        long userId=LoginInterceptor.threadLocal.get().getId();
        return labelMapper.selectList(new QueryWrapper<LabelDO>().eq("user_id",userId));
    }

    @Override
    public LabelDO findById(long id) {
        long userId=LoginInterceptor.threadLocal.get().getId();
        return labelMapper.selectOne(new QueryWrapper<LabelDO>().eq("id",id).eq("user_id",userId));
    }
}
