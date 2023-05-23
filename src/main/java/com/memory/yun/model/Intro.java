package com.memory.yun.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author NJUPT wly
 * @Date 2023/3/29 9:09 下午
 * @Version 1.0
 */
@Data
public class Intro {

    @JsonProperty("team_id")
    private Long teamId;

    private List<Long> labels;
}
