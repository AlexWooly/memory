package com.memory.yun.controller;

import com.memory.yun.request.IntroductionRequest;
import com.memory.yun.stragy.Introduce;
import com.memory.yun.stragy.IntroduceService;
import com.memory.yun.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author NJUPT wly
 * @Date 2023/2/3 12:52 上午
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/export")
public class IntroductionController {

    @Autowired
    Introduce introduce;

    @PostMapping("intro")
    public JsonData introduce(@RequestBody IntroductionRequest request){
        return JsonData.buildSuccess(introduce.introduce(request));
    }


}
