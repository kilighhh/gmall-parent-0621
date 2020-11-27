package com.atguigu.gmall.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Kilig Zong
 * @Date 2020/11/27 18:02
 * @Version 1.0
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
public class ServiceTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTestApplication.class,args);
    }
}
