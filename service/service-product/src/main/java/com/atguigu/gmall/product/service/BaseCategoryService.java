package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/28 11:35
 * @Version 1.0
 */
public interface BaseCategoryService {
    List<BaseCategory1> getCategory1List();


    List<BaseCategory2> getCategory2List(Long category1Id);

    List<BaseCategory3> getCategory3List(Long category2Id);

    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);

    List<JSONObject> getBaseCategoryList();
}
