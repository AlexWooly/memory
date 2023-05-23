package com.memory.yun.service;

import com.memory.yun.model.LabelDO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
public interface LabelService {
    int addLabel(String color, String name, Integer team1);

    int inerAddLabel(LabelDO labelDO);


    int update(LabelDO labelDO);

    int delLabel(long labelId);

    int delByteam(long teamId);

    List<LabelDO> find(long teamId);

    List<LabelDO> findAll();

    LabelDO findById(long id);
}
