package com.memory.yun.vo;

import lombok.Data;

/**
 * @Author NJUPT wly
 * @Date 2021/10/5 10:17 下午
 * @Version 1.0
 */
@Data
public class UserInfo {
    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 性别 0-未知 1-male,2-female
     */
    private Integer gender;

    /**
     * 头像地址
     */
    private String avatarUrl;

    private String country;

    private String province;

    private String city;

    /**
     * zh_CN简体中文 && zh_TW繁体中文
     */
    private String language;
}
