package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Kilig Zong
 * @Date 2020/12/1 20:25
 * @Version 1.0
 */
@Controller
public class ItemController {
    @RequestMapping("/")
    public String index(){

        return "index";
    }



    @RequestMapping("test")
    public String test(Model model){

        model.addAttribute("hello","hello thymeleaf");
        return "test";
    }
}
