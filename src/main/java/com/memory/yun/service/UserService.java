package com.memory.yun.service;

import com.memory.yun.model.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
public interface UserService {


    /**
     * 通过id查找用户
     * @return
     */
    UserDO findUserDetailById();

}
