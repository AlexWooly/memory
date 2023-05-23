package com.memory.yun.service;

import com.memory.yun.model.RecordDO;
import com.memory.yun.vo.RecordVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
public interface RecordService {

    int add(RecordVO recordVO);

    int init_add(RecordVO recordVO,long userId);

    int del(long recordId);

    int delByTeam(long teamId);

    int delByLabel(long labelId,long userId);

    Map<String, Object> find(int page, int size, String startTime, String endTime, List<Integer> teams);

    int update(RecordDO recordDO);

    List<RecordDO> findByTeamAndLabel(long teamId, long label, long userId,String startTime, String endTime);

}
