package com.atguigu.gmall.list.client;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:35
 * @Version 1.0
 */
@FeignClient(value = "service-list")
public interface ListFeignClient {
    /***
     * @author Kilig Zong
     * @date 2020/12/7 17:48
     * @description 首页一级二级三级分类数据
     * @return java.lang.String
     **/
    @RequestMapping("api/list/getBaseCategoryList")
    List<JSONObject> getBaseCategoryList();
    @RequestMapping("api/list/cancelSale/{skuInfoId}")
    void cancelSale(@PathVariable("skuInfoId") Long skuInfoId);
    @RequestMapping("api/list/onSale/{skuInfoId}")
    void onSale(@PathVariable("skuInfoId") Long skuInfoId);
    @RequestMapping("api/list/list")
    SearchResponseVo list(@RequestBody SearchParam searchParam);
    @RequestMapping("api/list/hotScore/{skuId}")
    void hotScore(@PathVariable("skuId") Long skuId);
}
