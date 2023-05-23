package com.memory.yun.stragy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memory.yun.interceptor.LoginInterceptor;
import com.memory.yun.mapper.LabelMapper;
import com.memory.yun.mapper.RecordMapper;
import com.memory.yun.mapper.TeamMapper;
import com.memory.yun.model.Intro;
import com.memory.yun.model.LoginUser;
import com.memory.yun.model.RecordDO;
import com.memory.yun.request.IntroductionRequest;
import com.memory.yun.service.LabelService;
import com.memory.yun.service.RecordService;
import com.memory.yun.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author NJUPT wly
 * @Date 2023/2/2 4:25 下午
 * @Version 1.0
 */
@Service
@Slf4j
public class IntroduceService implements Introduce{

    @Autowired
    LabelMapper labelMapper;

    @Autowired
    TeamMapper teamMapper;

    @Autowired
    RecordMapper recordMapper;

    @Autowired
    RecordService recordService;

     @Override
     public String introduce(IntroductionRequest request){
         LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        List<Intro> rest = request.getChoice();
        List<List<RecordDO>> allRecords = new ArrayList<>();
        for (Intro r : rest){
            Long teamId = r.getTeamId();
            List<Long> labels = r.getLabels();
            List<RecordDO> records = new ArrayList<>();
            for (long labelId:labels){
                List<RecordDO> byTeamAndLabel = recordService.findByTeamAndLabel(teamId, labelId, loginUser.getId(), startTime, endTime);
                records.addAll(byTeamAndLabel);
            }
            allRecords.add(records);
        }

        List<Integer> inclusion = request.getInclusion();
        Integer order = request.getOrder();
        String type = request.getType();
        if ("文本".equals(type)){
            StringBuilder introduction = new StringBuilder();
            //按成就组
            if (order.equals(1)){
                for (List<RecordDO> records:allRecords){
                    List<RecordDO> recordTime = records.stream().sorted(Comparator.comparing(RecordDO::getEndTime)).collect(Collectors.toList());
                    if (inclusion.contains(2)){
                        introduction.append(records.get(0).getTeam1());
                        introduction.append("\n");
                    }
                    for (RecordDO recordDO:recordTime){
                        if (inclusion.contains(1)){
                            introduction.append(CommonUtil.formatCh(recordDO.getEndTime()));
                            introduction.append(" ");
                        }
                        if (inclusion.contains(3)){
                            introduction.append(recordDO.getLabel());
                            if (inclusion.contains(4)){
                                introduction.append(":");
                            }
                        }
                        if (inclusion.contains(4)){
                            introduction.append(recordDO.getIntroduction());
                        }
                        introduction.append("\n");
                    }
                }
                return introduction.toString();
            }
            //按时间顺序
            else if (order.equals(2)){
                List<RecordDO> records = new ArrayList<>();
                for (List<RecordDO> recordDO:allRecords){
                    records.addAll(recordDO);
                }
                List<RecordDO> recordTime = records.stream().sorted(Comparator.comparing(RecordDO::getEndTime)).collect(Collectors.toList());
                for (RecordDO recordDO : recordTime){
                    if (inclusion.contains(1)){
                        introduction.append(CommonUtil.formatCh(recordDO.getEndTime()));
                        introduction.append(" ");
                    }
                    if (inclusion.contains(2)){
                        introduction.append(recordDO.getTeam1());
                        if (inclusion.contains(3)){
                            introduction.append(",");
                        }else {
                            introduction.append(":");
                        }
                    }
                    if (inclusion.contains(3)){
                        introduction.append(recordDO.getLabel());
                        if (inclusion.contains(4)){
                            introduction.append(":");
                        }
                    }
                    if (inclusion.contains(4)){
                        introduction.append(recordDO.getIntroduction());
                    }
                    introduction.append("\n");
                }
                return introduction.toString();
            }else {
                return null;
            }
        }else {
            return null;
        }
    }


}
