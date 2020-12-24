package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/14 12:15
 * @Version 1.0
 */
@RestController
@RequestMapping("api/cart")
public class CartApiController {
    @Autowired
    private CartService cartService;
    /***
     * @author Kilig Zong
     * @date 2020/12/14 13:17
     * @description 添加到购物车功能
     * @param cartInfo
     * @return void
     **/
    @RequestMapping("addCart")
   public void addCart(@RequestBody CartInfo cartInfo, HttpServletRequest request){
        //这个用户id是得用网关从其他微服务获得
        String userId=request.getHeader("userId");
        cartInfo.setUserId(userId);
        cartService.addCart(cartInfo);
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/14 13:24
     * @description 根据我们的用户id查询数据库的相关的数据 结算后的话就需要将这条数据一处
     * @param
     * @return com.atguigu.gmall.common.result.Result
     **/
    //http://api.gmall.com:8201/api/cart/cartList
    @RequestMapping("cartList")
    public Result cartList( HttpServletRequest request){
        //这个用户id是得用网关从其他微服务获得
        String userId=request.getHeader("userId");
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos= cartService.cartList(cartInfo);
       return Result.ok(cartInfos);
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/14 18:43
     * @description 更改我们的购物车的商品的数量
     * @param skuId
     * @param skuNum
     * @return com.atguigu.gmall.common.result.Result
     **/
    //http://api.gmall.com:8201/api/cart/addToCart/1/1
    @RequestMapping("addToCart/{skuId}/{skuNum}")
        public Result addToCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum,HttpServletRequest request){
        //这个用户id是得用网关从其他微服务获得
        String userId=request.getHeader("userId");
        //这里我们是需要将用户id 商品skuId还有数量传进数据库修改然后前端再次查询
       cartService.addToCart(skuId,skuNum,userId);
         return Result.ok();

    }
    /**
     * @author Kilig Zong
     * @date 2020/12/14 21:21
     * @description 根据√选择更新我们的数据
     * @param skuId
     * @param isChecked
     * @return com.atguigu.gmall.common.result.Result
     **/
    //http://api.gmall.com/api/cart/checkCart/1/1
    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId") Long skuId,@PathVariable("isChecked") Integer isChecked){
        //这个用户id是得用网关从其他微服务获得
        String userId="1";
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        cartInfo.setIsChecked(isChecked);
        cartService.checkCart(cartInfo);
        return Result.ok();
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/18 13:04
     * @description 根据我们的userId来查询我们的购物车的商品信息
     * @param userId
     * @return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     **/
    @RequestMapping("cartList/{userId}")
    public List<CartInfo> cartList(@PathVariable("userId") String userId){
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos = cartService.cartList(cartInfo);
        return cartInfos;
    }

}
