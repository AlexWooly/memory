package com.memory.yun.service.impl;

import com.memory.yun.interceptor.LoginInterceptor;
import com.memory.yun.model.FeedbackDO;
import com.memory.yun.mapper.FeedbackMapper;
import com.memory.yun.model.LoginUser;
import com.memory.yun.service.FeedbackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    FeedbackMapper feedbackMapper;

    @Override
    public int insert(FeedbackDO feedbackDO) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        feedbackDO.setCreateTime(new Date());
        feedbackDO.setUserId(loginUser.getId());
        return feedbackMapper.insert(feedbackDO);
    }
}
