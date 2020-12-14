package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/14 12:19
 * @Version 1.0
 */
public interface CartService {
    void addCart(CartInfo cartInfo);

    List<CartInfo> cartList(CartInfo cartInfo);

    void addToCart(Long skuId, Integer skuNum, String userId);

    void checkCart(CartInfo cartInfo);
}
