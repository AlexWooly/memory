package com.memory.yun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.yun.interceptor.LoginInterceptor;
import com.memory.yun.model.LoginUser;
import com.memory.yun.model.RecordDO;
import com.memory.yun.mapper.RecordMapper;
import com.memory.yun.service.RecordService;
import com.memory.yun.util.CommonUtil;
import com.memory.yun.vo.RecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
@Service
@Slf4j
public class RecordServiceImpl implements RecordService {

    @Autowired
    RecordMapper recordMapper;

    @Override
    public int add(RecordVO recordVO) {
        LoginUser user = LoginInterceptor.threadLocal.get();

        RecordDO recordDO = new RecordDO();
        recordDO.setLabel(recordVO.getLabel());
        recordDO.setName(recordVO.getName());
        recordDO.setIntroduction(recordVO.getIntroduction());
        recordDO.setTeam1(recordVO.getTeam1());
        recordDO.setCreateTime(new Date());
        recordDO.setEndTime(recordVO.getEndTime());
        recordDO.setUpdateTime(new Date());
        recordDO.setDel(1);
        recordDO.setUserId(user.getId());
        return recordMapper.insert(recordDO);
    }

    @Override
    public int init_add(RecordVO recordVO, long userId) {
        RecordDO recordDO = new RecordDO();
        recordDO.setLabel(recordVO.getLabel());
        recordDO.setName(recordVO.getName());
        recordDO.setIntroduction(recordVO.getIntroduction());
        recordDO.setTeam1(recordVO.getTeam1());
        recordDO.setCreateTime(new Date());
        recordDO.setEndTime(recordVO.getEndTime());
        recordDO.setUpdateTime(new Date());
        recordDO.setDel(1);
        recordDO.setUserId(userId);
        return recordMapper.insert(recordDO);
    }

    @Override
    public int del(long recordId) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        RecordDO recordDO = recordMapper.selectOne(new QueryWrapper<RecordDO>().eq("id", recordId).eq("user_id", user.getId()).eq("del",1));
        if (recordDO == null){
            return -1;
        }else {
            recordDO.setDel(0);
            recordMapper.updateById(recordDO);
            return 1;
        }
    }

    @Override
    public int delByTeam(long teamId) {
        LoginUser user = LoginInterceptor.threadLocal.get();

        List<RecordDO> records = recordMapper.selectList(new QueryWrapper<RecordDO>().eq("user_id", user.getId()).eq("team1", teamId));
        for (RecordDO recordDO:records){
            recordMapper.update(recordDO,new UpdateWrapper<RecordDO>().set("del",0));
        }
        return 1;
    }

    @Override
    public int delByLabel(long labelId, long userId) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        if (userId!=user.getId()){
            return -1;
        }
        List<RecordDO> records = recordMapper.selectList(new QueryWrapper<RecordDO>().eq("user_id", userId).eq("label", labelId));
        for (RecordDO recordDO:records){
            recordMapper.update(recordDO,new UpdateWrapper<RecordDO>().set("del",0));
        }
        return 1;
    }

    @Override
    public Map<String, Object> find(int page, int size, String startTime, String endTime, List<Integer> teams) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        if (startTime == null){
            long ms = System.currentTimeMillis() - 365*24*3600*1000L;
            startTime = CommonUtil.format(new Date(ms));
        }
        if (endTime == null){
            long ms = System.currentTimeMillis();
            endTime = CommonUtil.format(new Date(ms));
        }

        Page<RecordDO> pageInfo = new Page<>(page,size);
        IPage<RecordDO> recordPage = null;
        if (teams==null || teams.isEmpty()){
            recordPage = recordMapper.selectPage(pageInfo, new QueryWrapper<RecordDO>().eq("user_id",user.getId()).ge("end_time",startTime).le("end_time",endTime).eq("del",1));
        }else {
            recordPage = recordMapper.selectPage(pageInfo, new QueryWrapper<RecordDO>().in("team1",teams).eq("user_id",user.getId()).ge("end_time",startTime).le("end_time",endTime).eq("del",1));
        }

        if (recordPage==null){
            return new HashMap<String,Object>();
        }
        List<RecordDO> productOrderDOList = recordPage.getRecords();

        Map<String,Object> map = new HashMap<>(3);
        map.put("total_record",recordPage.getTotal());
        map.put("total_page",recordPage.getPages());
        map.put("current_data",productOrderDOList);

        return map;

    }

    @Override
    public int update(RecordDO recordDO) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        if (recordDO.getUserId()==user.getId()){
            return recordMapper.updateById(recordDO);
        }
        return -1;
    }

    @Override
    public List<RecordDO> findByTeamAndLabel(long teamId, long label,long userId, String startTime, String endTime) {
        LoginUser user = LoginInterceptor.threadLocal.get();
        if (userId!=user.getId()){
            return null;
        }
        if (startTime == null){
            long ms = System.currentTimeMillis() - 365*24*3600*1000L;
            startTime = CommonUtil.format(new Date(ms));
        }
        if (endTime == null){
            long ms = System.currentTimeMillis();
            endTime = CommonUtil.format(new Date(ms));
        }

        List<RecordDO> records = recordMapper.selectList(new QueryWrapper<RecordDO>().eq("user_id",user.getId()).ge("end_time",startTime).le("end_time",endTime).eq("del",1).eq("team1",teamId).eq("label",label));
        return records;
    }
}
