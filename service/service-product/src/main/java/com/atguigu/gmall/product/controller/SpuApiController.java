package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseAttrService;

import com.atguigu.gmall.product.service.SupInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/30 11:07
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class SpuApiController {
    @Autowired
    private SupInfoService supInfoService;

    @Autowired
    private BaseAttrService baseAttrService;
    /***
     * @author Kilig Zong
     * @date 2020/11/30 11:14
     * @description 注意这里需要返回的是一个Page 分页查询
     * @param pageNo
     * @param pageSize
     * @param category3Id
     * @return com.atguigu.gamll.common.result.Result
     **/
    @RequestMapping("{pageNo}/{pageSize}")
    public Result spuInfoList(@PathVariable Long pageNo,@PathVariable Long pageSize,Long category3Id){
        IPage<SpuInfo> infoIPage = supInfoService.spuInfoList(pageNo,pageSize,category3Id);
        return Result.ok(infoIPage);
    }
    /***
     * @author Kilig Zong
     * @date 2020/11/30 12:02
     * @description 查询平台销售属性
     * @param
     * @return com.atguigu.gamll.common.result.Result
     **/
    @RequestMapping("baseSaleAttrList")
    public  Result baseSaleAttrList(){
     List<BaseSaleAttr> baseSaleAttrList =baseAttrService.baseSaleAttrList();
     return Result.ok(baseSaleAttrList);

    }

    /***
     * @author Kilig Zong
     * @date 2020/11/30 12:14
     * @description 保存商家的编辑的spu的信息
     * @param spuInfo
     * @return com.atguigu.gamll.common.result.Result
     **/
    @RequestMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        supInfoService.saveSpuInfo(spuInfo);
        return  Result.ok();
    }

}
