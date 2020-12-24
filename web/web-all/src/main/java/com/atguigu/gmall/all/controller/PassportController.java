package com.atguigu.gmall.all.controller;

import com.alibaba.nacos.client.naming.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 11:40
 * @Version 1.0
 */
@Controller
public class PassportController{

    /***
     * @author Kilig Zong
     * @date 2020/12/18 20:28
     * @description 白名单需要登录的都会来这里
     * @param originUrl
     * @param model
     * @param request
     * @return java.lang.String
     **/
    @RequestMapping("login.html")
    public String login(String originUrl, Model model, HttpServletRequest request){
        //获得我们原来的所有的请求的地址
        //因为springmvc的原因我们的springmvc会根据&符号截取我们的参数,会把我们的skuNum给吞掉
        String queryString = request.getQueryString();
        int indexOf = queryString.indexOf("=");
        originUrl = queryString.substring(indexOf+1);

        model.addAttribute("originUrl",originUrl);

        return "login";
    }
}
