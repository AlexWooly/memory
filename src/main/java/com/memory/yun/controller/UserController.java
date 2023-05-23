package com.memory.yun.controller;


import com.memory.yun.model.FeedbackDO;
import com.memory.yun.model.UserDO;
import com.memory.yun.request.WxProfileRequest;
import com.memory.yun.service.FeedbackService;
import com.memory.yun.service.UserService;
import com.memory.yun.service.WxService;
import com.memory.yun.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wly
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    WxService wxService;

    @Autowired
    UserService userService;

    @Autowired
    FeedbackService feedbackService;

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    public JsonData login(@RequestBody WxProfileRequest wxProfileRequest){
        return wxService.login(wxProfileRequest);
    }

    /**
     *个人信息查询
     */
    @GetMapping("detail")
    public JsonData detail(){

        UserDO userDO = userService.findUserDetailById();

        return JsonData.buildSuccess(userDO);
    }

    /**
     * 游客登陆
     * @return
     */
    @GetMapping("getOpenId")
    private JsonData getOpenId(String code){
        return wxService.getOpenId(code);
    }

}

