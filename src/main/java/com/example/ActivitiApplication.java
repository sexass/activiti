package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @Author:郭友文
 * @Data:2018/7/25 11:01
 * @Description:
 */

@SpringBootApplication
@EnableConfigurationProperties
public class ActivitiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActivitiApplication.class,args);
    }
}
