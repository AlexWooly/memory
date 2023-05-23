package com.memory.yun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author NJUPT wly
 * @Date 2022/11/22 5:35 下午
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.memory.yun.mapper")
public class YunApplication {
    public static void main(String[] args) {
        SpringApplication.run(YunApplication.class, args);
    }
}
