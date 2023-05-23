package com.memory.yun.controller;

import com.memory.yun.model.FeedbackDO;
import com.memory.yun.service.FeedbackService;
import com.memory.yun.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author NJUPT wly
 * @Date 2023/1/5 1:29 下午
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    FeedbackService feedbackService;

    @PostMapping("addFeedback")
    public JsonData insert(FeedbackDO feedbackDO){
        int res = feedbackService.insert(feedbackDO);
        return res ==1?JsonData.buildSuccess("反馈成功"):JsonData.buildError("反馈失败");
    }

}
