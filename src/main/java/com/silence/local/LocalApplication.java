package com.silence.local;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.silence.local.*", "com.silence.module.db.*"})
@MapperScan(value = {"com.silence.module.db.support.mapper","com.silence.module.common.mapper"})
public class LocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalApplication.class, args);
        System.out.println("----------- success ------------");
    }

}
