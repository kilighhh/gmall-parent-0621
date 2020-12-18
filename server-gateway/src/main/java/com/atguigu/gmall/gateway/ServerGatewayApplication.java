package com.atguigu.gmall.gateway;

import com.atguigu.gmall.common.config.FeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 10:22
 * @Version 1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(value = {"com.atguigu.gmall"},excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = FeignInterceptor.class))
@EnableDiscoveryClient//让注册中心发现服务
@EnableFeignClients("com.atguigu.gmall")//开启feign远程调用
public class ServerGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerGatewayApplication.class,args);
    }
}
