package com.atguigu.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * @Author Kilig Zong
 * @Date 2020/12/2 12:04
 * @Version 1.0 association collection Executor
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
@EnableDiscoveryClient//让注册中心发现服务
@EnableFeignClients("com.atguigu.gmall")//开启feign远程调用
public class ServicePaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePaymentApplication.class,args);
    }
}
