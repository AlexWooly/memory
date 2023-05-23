package com.memory.yun.controller;

import com.memory.yun.mapper.LabelMapper;
import com.memory.yun.mapper.RecordMapper;
import com.memory.yun.mapper.TeamMapper;
import com.memory.yun.model.RecordDO;
import com.memory.yun.service.LabelService;
import com.memory.yun.service.RecordService;
import com.memory.yun.service.TeamService;
import com.memory.yun.util.JsonData;
import com.memory.yun.vo.RecordVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author NJUPT wly
 * @Date 2023/1/10 11:13 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/record")
@Slf4j
public class RecordController {

    @Autowired
    TeamService teamService;

    @Autowired
    RecordService recordService;

    @Autowired
    LabelService labelService;

    @PostMapping("addRecord")
    public JsonData addRecoed(@RequestBody RecordVO recordVO){
        int res = recordService.add(recordVO);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("添加记录失败");
    }

    @PostMapping("updateRecord")
    public JsonData updateRecord(@RequestBody RecordDO recordDO){
        int res = recordService.update(recordDO);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("更新记录失败");
    }

    @GetMapping("findRecord")
    public JsonData findRecord(@RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "size",defaultValue = "10") int size,@RequestParam("start_time") String startTime, @RequestParam("end_time")String endTime,
                               @RequestParam(value = "teams") String teams){
        List<Integer> list = null;

        if ("".equals(teams.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", ""))){
        }else {
            String[] items = teams.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

            int[] results = new int[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    results[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {
                    log.error("数组转换出错");
                };
            }
            list = Arrays.asList(ArrayUtils.toObject(results));
        }

        Map<String, Object> map = recordService.find(page, size, startTime, endTime,list);
        return JsonData.buildSuccess(map);
    }

    @GetMapping("delRecord")
    public JsonData delRecord(@RequestParam("record_id")int recordId){
        int res = recordService.del(recordId);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("删除记录失败");
    }

}
