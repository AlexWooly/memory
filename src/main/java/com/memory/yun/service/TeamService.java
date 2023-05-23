package com.memory.yun.service;

import com.memory.yun.model.LabelDO;
import com.memory.yun.model.TeamDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
public interface TeamService {

    int addTeam(String name);

    int innerAddTeam(TeamDO teamDO);

    int update(TeamDO teamDO);

    int delTeam(long labelId);

    List<TeamDO> findAll();

    TeamDO findById(long id);

}
