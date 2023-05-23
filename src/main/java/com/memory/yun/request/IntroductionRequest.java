package com.memory.yun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memory.yun.model.Intro;
import lombok.Data;

import java.util.*;

/**
 * @Author NJUPT wly
 * @Date 2023/2/2 4:39 下午
 * @Version 1.0
 */
@Data
public class IntroductionRequest {

    private String type;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    private List<Intro> choice;

    private List<Integer> inclusion;

    private Integer order;

}
