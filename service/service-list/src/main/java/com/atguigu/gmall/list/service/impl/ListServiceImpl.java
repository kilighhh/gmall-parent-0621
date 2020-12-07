package com.atguigu.gmall.list.service.impl;
import com.atguigu.gmall.product.clent.ProductFeignClient;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:51
 * @Version 1.0
 */
@Service
public class ListServiceImpl implements ListService {
    @Autowired
    private ProductFeignClient productFeignClient;
    /***
     * @author Kilig Zong
     * @date 2020/12/7 17:59
     * @description 获取首页的一二三级分类数据
     * @param
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     **/
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list=  productFeignClient.getBaseCategoryList();
        return list;
    }
}
