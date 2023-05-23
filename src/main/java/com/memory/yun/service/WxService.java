package com.memory.yun.service;


import com.memory.yun.request.WxProfileRequest;
import com.memory.yun.util.JsonData;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author NJUPT wly
 * @Date 2021/10/5 6:58 下午
 * @Version 1.0
 */
public interface WxService {

    /**
     * 获取openid
     * @param code
     * @return
     */
    JsonData getOpenId(String code);

    /**
     * 授权登陆
     * @param request
     * @return
     */
    JsonData login(WxProfileRequest request);


    /**
     * 图片检查
     * @param file
     * @return
     */
    JsonData checkImg(MultipartFile file);


}
