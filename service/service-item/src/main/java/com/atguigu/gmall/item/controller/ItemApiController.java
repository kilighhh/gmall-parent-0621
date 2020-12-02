package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/2 12:09
 * @Version 1.0
 */
@RequestMapping("api/item")
@RestController
public class   ItemApiController{
    @Autowired
    private ItemService itemService;
    /***
     * @author Kilig Zong
     * @date 2020/12/2 12:14
     * @description 创建一个controller供给ItemFeignClient调用
     * 在service层的时候将各个值封装成map返回给web
     * @param skuId
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @RequestMapping("getItem/{skuId}")
    public Map<String, Object> getItem(@PathVariable("skuId") Long skuId){
        Map<String, Object> map= itemService.getItem(skuId);
        return map;
    }


}
