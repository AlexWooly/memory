package com.memory.yun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memory.yun.interceptor.LoginInterceptor;
import com.memory.yun.mapper.LabelMapper;
import com.memory.yun.model.LabelDO;
import com.memory.yun.model.LoginUser;
import com.memory.yun.model.TeamDO;
import com.memory.yun.mapper.TeamMapper;
import com.memory.yun.service.LabelService;
import com.memory.yun.service.RecordService;
import com.memory.yun.service.TeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
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
@Slf4j
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamMapper teamMapper;

    @Autowired
    LabelMapper labelMapper;

    @Autowired
    RecordService recordService;

    @Autowired
    LabelService labelService;

    @Override
    public int addTeam(String name) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        TeamDO team = teamMapper.selectOne(new QueryWrapper<TeamDO>().eq("user_id",loginUser.getId()).eq("name",name));

        if (team!=null){
            log.error("team存在:{}",team);
            return -1;
        }
        TeamDO teamDO = new TeamDO();

        teamDO.setUserId(loginUser.getId());
        teamDO.setName(name);
        return teamMapper.insert(teamDO);
    }

    @Override
    public int innerAddTeam(TeamDO teamDO) {
        return teamMapper.insert(teamDO);
    }

    @Override
    public int update(TeamDO teamDO) {
        if (teamDO.getUserId()!=LoginInterceptor.threadLocal.get().getId()){
            log.error("team:{}",teamDO);
            return -1;
        }
        TeamDO team = teamMapper.selectOne(new QueryWrapper<TeamDO>().eq("user_id",teamDO.getUserId()).eq("name",teamDO.getName()));

        if (team!=null&&team.getId()!=teamDO.getId()){
            log.error("team冲突，team:{}，teamDo:{}",team,teamDO);
            return -1;
        }
        return teamMapper.updateById(teamDO);
    }

    @Override
    @Transactional
    public int delTeam(long teamId) {
        long userId = LoginInterceptor.threadLocal.get().getId();
        labelService.delByteam(teamId);
        teamMapper.delete(new QueryWrapper<TeamDO>().eq("id",teamId).eq("user_id",userId));
        return 1;
    }

    @Override
    public List<TeamDO> findAll() {
        long userId = LoginInterceptor.threadLocal.get().getId();
        return teamMapper.selectList(new QueryWrapper<TeamDO>().eq("user_id",userId));
    }

    @Override
    public TeamDO findById(long id) {
        long userId = LoginInterceptor.threadLocal.get().getId();

        return teamMapper.selectOne(new QueryWrapper<TeamDO>().eq("id",id).eq("user_id",userId));
    }
}
