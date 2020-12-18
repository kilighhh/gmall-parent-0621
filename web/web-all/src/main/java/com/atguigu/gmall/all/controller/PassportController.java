package com.atguigu.gmall.all.controller;

import com.alibaba.nacos.client.naming.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 11:40
 * @Version 1.0
 */
@Controller
public class PassportController{

    @RequestMapping("login.html")
    public String login(String originUrl, Model model){
//
//        if(StringUtils.isEmpty(originUrl)){
//            originUrl = "http://www.gmall.com";
//        }

        model.addAttribute("originUrl",originUrl);

        return "login";
    }
}
