package com.atguigu.gmall.list.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:40
 * @Version 1.0
 */
@RequestMapping("api/list")
@RestController
public class ListApiController {

    @Autowired
    private ListService listService;

    /***
     * @author Kilig Zong
     * @date 2020/12/7 17:50
     * @description 首页一级二级三级分类数据
     * @param
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     **/
    @RequestMapping("getBaseCategoryList")
    public List<JSONObject> getBaseCategoryList(){
        List<JSONObject> list= listService.getBaseCategoryList();
        return list;
    }
}
