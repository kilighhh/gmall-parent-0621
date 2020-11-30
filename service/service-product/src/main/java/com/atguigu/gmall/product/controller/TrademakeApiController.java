package com.atguigu.gmall.product.controller;

import com.atguigu.gamll.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/30 11:45
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class TrademakeApiController {
    @Autowired
    private BaseTrademarkService trademarkService;

    @RequestMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
    List<BaseTrademark> trademarks = trademarkService.getTrademarkList();
    return Result.ok(trademarks);
    }
}
