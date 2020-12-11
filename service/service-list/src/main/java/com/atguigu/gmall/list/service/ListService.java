package com.atguigu.gmall.list.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

import java.text.ParseException;
import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:45
 * @Version 1.0
 */
public interface ListService {
    List<JSONObject> getBaseCategoryList();

    void cancelSale(Long skuInfoId);

    void onSale(Long skuInfoId) throws  Exception;

    void createGoodsIndex();

    SearchResponseVo list(SearchParam searchParam);
}
