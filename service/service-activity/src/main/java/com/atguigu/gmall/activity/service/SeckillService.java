package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/23 18:57
 * @Version 1.0
 */
public interface SeckillService {
    void putGoods(Long skuId);

    List<SeckillGoods> findAll();

    SeckillGoods getItem(Long skuId);

    Map<String, Object> seckillOrder(Long skuId, String userId);

    OrderRecode checkOrderRecode(String userId);

    String checkTrueOrder(String userId);

    OrderRecode getOrderRecode(String userId);

    void deleteOrderRecode(String userId);

    void genOrderUsers(String userId, String orderId);

}
