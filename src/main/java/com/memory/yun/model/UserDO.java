package com.memory.yun.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wly
 * @since 2023-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @JsonProperty("nick_name")
    private String nickName;

    private String openId;

    private Integer gender;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String country;

    private String province;

    private String city;

    private String language;

    private Long points;

    private Date createTime;


}
