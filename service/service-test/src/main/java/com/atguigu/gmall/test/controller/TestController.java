package com.atguigu.gmall.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Kilig Zong
 * @Date 2020/11/27 18:05
 * @Version 1.0
 */
@RequestMapping("api/test/")
@RestController
public class TestController {

    @GetMapping("test")
    public String test(){
        return "hello,world";
    }
}
