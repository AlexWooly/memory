package com.memory.yun.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wly
 * @since 2022-11-25
 */
@Data
public class RecordVO implements Serializable {

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

    /**
     * 任务截止时间
     */
    @JsonProperty("end_time")
    private Date endTime;

    /**
     * 任务状态，0计划1完成2逾期3删除/取消
     */
    private Integer state;


}
