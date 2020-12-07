package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SupInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/1 15:07
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class SkuApiController {

    @Autowired
    private SupInfoService supInfoService;
    @Autowired
    private SkuInfoService skuInfoService;

    /***
     * @author Kilig Zong
     * @date 2020/12/1 16:11
     * @description 查询spu的图片集合
     * @param spuId
     * @return com.atguigu.gamll.common.result.Result
     **/
   // http://localhost:8080/admin/product/spuImageList/1
    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){
       List<SpuImage> spuImageList = supInfoService.spuImageList(spuId);
       return Result.ok(spuImageList);
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/1 16:12
     * @description 查询spu销售属性的集合
     * @param spuId
     * @return com.atguigu.gamll.common.result.Result
     **/
    //http://localhost:8080/admin/product/spuSaleAttrList/1
    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
     List<SpuSaleAttr>  spuSaleAttrList =   supInfoService.spuSaleAttrList(spuId);
     return Result.ok(spuSaleAttrList);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/1 18:44
     * @description 保存skuInfo的数据到数据库，记得有四张表
     * @param skuInfo
     * @return com.atguigu.gamll.common.result.Result
     **/
    //http://localhost:8080/admin/product/saveSkuInfo
    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/1 18:47
     * @description 分页查询数据库的数据 返回的数据是page
     * @param pageNo
     * @param limit
     * @return com.atguigu.gamll.common.result.Result
     **/
    //http://localhost:8080/admin/product/list/1/10
    @RequestMapping("list/{pageNo}/{limit}")
    public Result list(@PathVariable("pageNo") Long pageNo,@PathVariable("limit") Long limit){
       IPage<SkuInfo> skuInfoPage = skuInfoService.list(pageNo,limit);
      //  System.out.println(pageNo+"===="+limit);
       return Result.ok(skuInfoPage);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/1 19:50
     * @description 商品下架功能 商品下架 未完成 清理nosql 使用es  同步搜索引擎
     * @param skuInfoId
     * @return com.atguigu.gamll.common.result.Result
     **/
    //http://localhost:8080/admin/product/cancelSale/1
    @RequestMapping("cancelSale/{skuInfoId}")
    public Result cancelSale(@PathVariable Long skuInfoId){
        boolean flag= skuInfoService.cancelSale(skuInfoId);
        //System.out.println("flag = " + flag);
        if(flag==true){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }
    /***
     * @author Kilig Zong
     * @date 2020/12/1 19:50
     * @description 商品上架功能 商品上架 未完成 写入nosql 使用es 同步搜索引擎
     * @param skuInfoId
     * @return com.atguigu.gamll.common.result.Result
     **/
    //http://localhost:8080/admin/product/onSale/2
    @RequestMapping("onSale/{skuInfoId}")
    public Result onSale(@PathVariable Long skuInfoId){
      boolean flag=skuInfoService.onSale(skuInfoId);
       // System.out.println("flag = " + flag);
      if(flag==true){
          return Result.ok();
      }else {
          return Result.fail();
      }
    }
}
