package com.memory.yun.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
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
@TableName("record")
public class RecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 成就组
     */
    private long team1;

    /**
     * 标签
     */
    private long label;

    /**
     * 简介
     */
    private String introduction;

    private Date updateTime;

    private Date createTime;

    /**
     * 任务截止时间
     */
    private Date endTime;

    /**
     * 系统内用户id
     */
    @JsonProperty("user_id")
    private long userId;

    /**
     * 0删除1正常
     */
    private Integer del;

    /**
     * 任务状态，0计划1完成2逾期3删除/取消
     */
    private Integer state;


}
