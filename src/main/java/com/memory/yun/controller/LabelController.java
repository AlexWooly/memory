package com.memory.yun.controller;

import com.memory.yun.model.LabelDO;
import com.memory.yun.model.RecordDO;
import com.memory.yun.service.LabelService;
import com.memory.yun.util.JsonData;
import com.memory.yun.vo.RecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * @Author NJUPT wly
 * @Date 2023/2/1 12:23 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/label")
public class LabelController {
    @Autowired
    LabelService labelService;

    @GetMapping("addLabel")
    public JsonData addLabel(@RequestParam("color")String color, @RequestParam("name")String name, @RequestParam("team1")Integer team1){
        int res = labelService.addLabel(color, name, team1);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("添加标签失败");
    }

    @PostMapping("updateLabel")
    public JsonData updateLabel(@RequestBody LabelDO labelDO){
        int res = labelService.update(labelDO);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("更新标签失败");
    }

    @GetMapping("findLabelByTeam")
    public JsonData findLabel(@RequestParam("team1_id")long team1){
        List<LabelDO> labelDOList = labelService.find(team1);
        return JsonData.buildSuccess(labelDOList);
    }

    @GetMapping("findLabelAll")
    public JsonData findLabelAll() {
        List<LabelDO> labelDOList = labelService.findAll();
        return JsonData.buildSuccess(labelDOList);
    }

    @GetMapping("delLabel")
    public JsonData delLabel(@RequestParam("label_id")long labelId){
        int res = labelService.delLabel(labelId);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("删除标签失败");
    }

    @GetMapping("detailLabel")
    public JsonData detailLabel(@RequestParam("label_id")long labelId){
        LabelDO labelDO = labelService.findById(labelId);
        return JsonData.buildSuccess(labelDO);
    }

}
