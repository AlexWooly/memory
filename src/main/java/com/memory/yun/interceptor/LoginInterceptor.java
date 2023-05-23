package com.memory.yun.interceptor;

import com.memory.yun.enums.BizCodeEnum;
import com.memory.yun.model.LoginUser;
import com.memory.yun.util.CommonUtil;
import com.memory.yun.util.JWTUtil;
import com.memory.yun.util.JsonData;
import io.jsonwebtoken.Claims;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author NJUPT wly
 * @Date 2021/9/15 11:33 下午
 * @Version 1.0
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("token");
        if (accessToken == null){
            accessToken = request.getParameter("token");
        }

        if (StringUtils.isNotBlank(accessToken)){
            Claims claims = JWTUtil.checkJWT(accessToken);
            //未登陆
            if (claims == null){
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }

            long userId = Integer.parseInt(claims.get("id").toString());
            String headImg = (String) claims.get("head_img");
            String name = (String) claims.get("name");
            String openid = (String) claims.get("openid");

            LoginUser loginUser = new LoginUser();
            loginUser.setId(userId);
            loginUser.setName(name);
            loginUser.setHeadImg(headImg);
            loginUser.setOpenId(openid);

            //通过request传递
            request.setAttribute("loginUser",loginUser);

            //通过threadlocal传递信息
            threadLocal.set(loginUser);

            return true;
        }

        CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
