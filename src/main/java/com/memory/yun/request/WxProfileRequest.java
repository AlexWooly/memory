package com.memory.yun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memory.yun.vo.UserInfo;

import lombok.Data;

/**
 * @Author NJUPT wly
 * @Date 2021/10/5 6:30 下午
 * @Version 1.0
 */
@Data
public class WxProfileRequest {

    private String code;

    @JsonProperty("raw_data")
    private String rawData;

    private String signature;

    @JsonProperty("encrypted_data")
    private String encryptedData;

    private String iv;

    @JsonProperty("user_info")
    private UserInfo userInfo;
}
