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
 * @since 2022-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("feedback")
public class FeedbackDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @JsonProperty("user_id")
    private long userId;

    private String detail;

    private Date createTime;


}
