package com.memory.yun.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author NJUPT wly
 * @Date 2021/9/15 7:07 下午
 * @Version 1.0
 */
@Data
public class LoginUser {
    private long id;

    private String name;

    @JsonProperty("head_img")
    private String headImg;

    @JsonProperty("open_id")
    private String openId;
}
