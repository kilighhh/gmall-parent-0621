package com.atguigu.gmall.all.controller;


import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Kilig Zong
 * @Date 2020/12/14 11:56
 * @Version 1.0
 */
@Controller
public class CartController {

    @Autowired
    private CartFeignClient cartFeignClient;

    @RequestMapping("addCart.html")
    public String addCart(Long skuId, Long skuNum, CartInfo cartInfo){
        cartFeignClient.addCart(cartInfo);
        return "redirect:http://cart.gmall.com/cart/addCart.html?skuNum="+cartInfo.getSkuNum();
        //return "redirect:/cart/addCart.html?skuNum="+cartInfo.getSkuNum();
    }

    @RequestMapping("cart/cart.html")
    public  String cartList(){
        //这里是通过一个用户id来查询我们的购物车列表
        String userId="1";
        return "cart/index";
    }
}
