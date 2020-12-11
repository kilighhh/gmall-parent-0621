package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    /***
     * @author Kilig Zong
     * @date 2020/12/11 11:10
     * @description 搜索框查询或者根据三级分类来进行搜索结果
     * @param model
     * @return java.lang.String
     **/
    @RequestMapping({"list.html","search.html"})
    public  String list(Model model, SearchParam searchParam){
      SearchResponseVo searchResponseVo=listFeignClient.list(searchParam);
        List<Goods> goodsList = searchResponseVo.getGoodsList();
        //上传商品的信息集合
        model.addAttribute("goodsList",goodsList);
        //上传logo的信息集合
        List<SearchResponseTmVo> trademarkList = searchResponseVo.getTrademarkList();
        model.addAttribute("trademarkList",trademarkList);
        return "list/index";
    }

}
