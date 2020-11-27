package com.atguigu.gmall.product.controller;

import com.atguigu.gamll.common.result.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Kilig Zong
 * @Date 2020/11/27 18:27
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class CategoryApiController {

    @GetMapping("getCategory1")
    public Result getCategory1(){
        return Result.ok();
    }

}
