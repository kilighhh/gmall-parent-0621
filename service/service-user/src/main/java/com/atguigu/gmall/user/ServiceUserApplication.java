package com.atguigu.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Kilig Zong
 * @Date 2020/11/27 18:25
 * @Version 1.0
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
@EnableDiscoveryClient//让注册中心发现服务
public class ServiceUserApplication{
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class,args);
    }
}
