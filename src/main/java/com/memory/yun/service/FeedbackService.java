package com.memory.yun.service;

import com.memory.yun.model.FeedbackDO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
public interface FeedbackService {

    int insert(FeedbackDO feedbackDO);

}
