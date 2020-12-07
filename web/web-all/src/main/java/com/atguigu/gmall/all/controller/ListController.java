package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.client.ListFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:22
 * @Version 1.0
 */
@Controller
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;
    /***
     * @author Kilig Zong
     * @date 2020/12/7 17:48
     * @description 首页一级二级三级分类数据
     * @param model
     * @return java.lang.String
     **/
    @RequestMapping("/")
    public  String index(Model model){
     List<JSONObject> list=listFeignClient.getBaseCategoryList();
    model.addAttribute("list",list);
     return "index/index";
    }

}
