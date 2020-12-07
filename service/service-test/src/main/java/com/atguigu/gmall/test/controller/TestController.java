package com.atguigu.gmall.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/11/27 18:05
 * @Version 1.0
 */
@RequestMapping("api/test/")
@RestController
public class TestController {
    // 缓存 ID 集合
    private Map<String, Integer> reqCache = new HashMap<>();

    @GetMapping("test/{id}")
    public String test(@PathVariable Integer id){

        return "hello,world";
    }
}
