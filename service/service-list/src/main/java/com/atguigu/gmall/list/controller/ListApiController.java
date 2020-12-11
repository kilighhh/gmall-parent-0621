package com.atguigu.gmall.list.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    /***
     * @author Kilig Zong
     * @date 2020/12/9 15:35
     * @description 商品下架功能，利用es
     * @param skuInfoId
     * @return void
     **/
    @RequestMapping("cancelSale/{skuInfoId}")
   public void cancelSale(@PathVariable("skuInfoId") Long skuInfoId){
        listService.cancelSale(skuInfoId);
        System.out.println("搜索系统下架商品");
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/9 15:36
     * @description 商品上架功能 利用es
     * @param skuInfoId
     * @return void
     **/
    @RequestMapping("onSale/{skuInfoId}")
   public void onSale(@PathVariable("skuInfoId") Long skuInfoId) throws Exception {
        listService.onSale(skuInfoId);
        System.out.println("搜索系统上架商品");
    }
    @RequestMapping("createGoodsIndex")
    public Result createGoodsIndex(){
        //创建数据搜索库
        listService.createGoodsIndex();
        return Result.ok();
    }
    /***
     * @author Kilig Zong
     * @date  11:21
     * @description 根据根据我们搜索的参数在es中进行搜索
     * @param searchParam
     * @return com.atguigu.gmall.model.list.SearchResponseVo
     **/
    @RequestMapping("list")
  public   SearchResponseVo list(@RequestBody SearchParam searchParam){
        SearchResponseVo searchResponseVo= listService.list(searchParam);
        return searchResponseVo;
    }
}
