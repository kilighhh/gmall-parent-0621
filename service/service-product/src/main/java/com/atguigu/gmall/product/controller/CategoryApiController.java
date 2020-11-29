package com.atguigu.gmall.product.controller;

import com.atguigu.gamll.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/27 18:27
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class CategoryApiController {

    @Autowired
    private BaseCategoryService baseCategoryService;
    //以下方法都是平台一级二级分类查询的方法接口
    @GetMapping("getCategory1")
    public Result getCategory1(){
    List<BaseCategory1> category1List=baseCategoryService.getCategory1List();
        return Result.ok(category1List);
    }
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable ("category1Id") Long category1Id){
        List<BaseCategory2> category2List=baseCategoryService.getCategory2List(category1Id);
        return Result.ok(category2List);
    }
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable ("category2Id") Long category2Id){
        List<BaseCategory3> category3List=baseCategoryService.getCategory3List(category2Id);
        return Result.ok(category3List);
    }


}
