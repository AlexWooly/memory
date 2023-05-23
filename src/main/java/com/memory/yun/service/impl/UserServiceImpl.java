package com.memory.yun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.memory.yun.interceptor.LoginInterceptor;
import com.memory.yun.model.LoginUser;
import com.memory.yun.model.UserDO;
import com.memory.yun.mapper.UserMapper;
import com.memory.yun.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;


    /**
     * id查找个人信息
     * @return
     */
    @Override
    public UserDO findUserDetailById() {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        return userMapper.selectOne(new QueryWrapper<UserDO>().eq("id",loginUser.getId()));
    }

}
