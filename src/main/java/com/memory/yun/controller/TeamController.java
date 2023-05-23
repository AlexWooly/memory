package com.memory.yun.controller;

import com.memory.yun.model.LabelDO;
import com.memory.yun.model.TeamDO;
import com.memory.yun.service.LabelService;
import com.memory.yun.service.TeamService;
import com.memory.yun.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author NJUPT wly
 * @Date 2023/2/1 12:23 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    TeamService teamService;

    @RequestMapping("addTeam")
    public JsonData addTeam( @RequestParam("name")String name){
        int res = teamService.addTeam(name);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("添加成就组失败");
    }

    @PostMapping("updateTeam")
    public JsonData updateTeam(@RequestBody TeamDO teamDO){
        int res = teamService.update(teamDO);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("更新成就组失败");
    }

    @GetMapping("findTeamById")
    public JsonData findTeam(@RequestParam("team1_id")long team1){
        TeamDO teamDO = teamService.findById(team1);
        return JsonData.buildSuccess(teamDO);
    }

    @GetMapping("findTeamAll")
    public JsonData findLabelAll() {
        List<TeamDO> teamDOList = teamService.findAll();
        return JsonData.buildSuccess(teamDOList);
    }

    @GetMapping("delTeam")
    public JsonData delLabel(@RequestParam("team1_id")int teamId){
        int res = teamService.delTeam(teamId);
        return res == 1 ?JsonData.buildSuccess():JsonData.buildError("删除成就组失败");
    }

}
