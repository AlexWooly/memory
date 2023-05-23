package com.memory.yun.enums;

import lombok.Getter;

/**
 * @Author NJUPT wly
 * @Date 2021/9/13 9:58 上午
 * @Version 1.0
 */
public enum BizCodeEnum {

    OPS_REPEAT(110001,"重复操作"),
    ACCOUNT_UNLOGIN(100001,"账号未登陆"),
    /**
     * 微信相关
     */
    WX_GET_ACCESS_TOKEN(700001,"获取access_token失败"),
    WX_GET_OPENID_FAIL(700101,"获取用户openId失败"),
    WX_GET_USERINFO_FAIL(700201,"获取用户信息失败"),

    WX_CHECK_IMG_FAIL(700301,"检查图片失败"),
    WX_CHECK_IMG_ERROR(700302,"图片违规");


    @Getter
    private String message;

    @Getter
    private int code;

    private BizCodeEnum(int code,String message){
        this.code = code;
        this.message = message;
    }
}
