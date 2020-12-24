package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;

    @RequestMapping("myOrder.html")
    public String myOrder(){
        return "order/myOrder";
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/18 12:10
     * @description 结算页面获取用户的收获地址以及获得我需要结算的商品详情（在购物车中获取）
     * @param model
     * @return java.lang.String
     **/
    //trade.html
    @RequestMapping("trade.html")
    public String trade(Model model, HttpServletRequest request){
        //userAddressList
        //detailArrayList
        //获得用户的id
        String userId = request.getHeader("userId");
        List<OrderDetail> detailArrayList   = orderFeignClient.getTrade();
        List<UserAddress> userAddressList = userFeignClient.findUserAddressListByUserId(userId);
        //生成我们的唯一的交易流水号放进我们的缓存
        String tradeNo=orderFeignClient.genTradeNo(userId);
        //返回一个用户地址
        model.addAttribute("userAddressList",userAddressList);
        //返回一个订单所拥有的sku来自于我们的购物车
        model.addAttribute("detailArrayList",detailArrayList);
        //返回订单的总价格
        BigDecimal totalAmount=getTotalAmount(detailArrayList);
        model.addAttribute("totalAmount",totalAmount);
        //返回唯一的交易流水号防止订单重复提交
        model.addAttribute("tradeNo",tradeNo);
        return "order/trade";
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/18 13:11
     * @description 获取商品的总价格
     * @param detailArrayList
     * @return java.math.BigDecimal
     **/
    private BigDecimal getTotalAmount(List<OrderDetail> detailArrayList) {
        BigDecimal totalAmount = new BigDecimal("0");
      if(null!=detailArrayList&&detailArrayList.size()>0){
          for (OrderDetail orderDetail : detailArrayList) {
              BigDecimal orderPrice = orderDetail.getOrderPrice();
              //获得商品的数量
              Integer skuNum = orderDetail.getSkuNum();
//              //获得同一件商品的总价格
            BigDecimal totalPrice = orderPrice.multiply(new BigDecimal(skuNum + ""));
              totalAmount=totalAmount.add(totalPrice);
          }
      }
        return totalAmount;
    }


}