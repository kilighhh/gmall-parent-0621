package com.atguigu.gmall.list.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:45
 * @Version 1.0
 */
public interface ListService {
    List<JSONObject> getBaseCategoryList();
}
