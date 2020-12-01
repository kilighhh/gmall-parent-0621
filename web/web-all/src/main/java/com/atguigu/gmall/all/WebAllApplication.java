package com.atguigu.gmall.all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Kilig Zong
 * @Date 2020/12/1 20:22
 * @Version 1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"com.atguigu.gmall"})
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class,args);
    }
}
