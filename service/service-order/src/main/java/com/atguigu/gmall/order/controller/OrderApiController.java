package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/18 12:00
 * @Version 1.0
 */
@RequestMapping("api/order")
@RestController
public class OrderApiController {
    @Autowired
    private OrderService orderService;
    /***
     * @author Kilig Zong
     * @date 2020/12/18 12:28
     * @description 根据userId远程调用获取我们的购物车的信息，然后转换成我们的OrderDetail集合
     * @param request
     * @return java.util.List<com.atguigu.gmall.model.order.OrderDetail>
     **/
    @RequestMapping("getTrade")
    public List<OrderDetail> getTrade(HttpServletRequest request){
        String userId = request.getHeader("userId");
        List<OrderDetail> orderDetails= orderService.getTrade(userId);
        return orderDetails;
    }



    /***
     * @author Kilig Zong
     * @date 2020/12/18 18:11
     * @description 我们的结算页面已经生成了，需要生成我们的订单
     * //http://api.gmall.com/api/order/auth/submitOrder?tradeNo=null
     * @param order
     * @param request
     * @param model
     * @return com.atguigu.gmall.common.result.Result
     **/
    @RequestMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo order,String tradeNo,HttpServletRequest request, Model model){
        //获得用户的id
        String userId = request.getHeader("userId");
        //检查我们的唯一交易流水号，确认后并且删除
        boolean flag=orderService.checkTradeNo(tradeNo,userId);
      if(flag){
          String orderId= orderService.submitOrder(order,userId);
          return Result.ok(orderId);
      }else {
          return Result.fail();
      }
    }


    /***
     * @author Kilig Zong
     * @date 2020/12/18 19:31
     * @description 获得我们的唯一交易流水号防止订单重复提交
     * @param userId
     * @return java.lang.String
     **/
    @RequestMapping("genTradeNo/{userId}")
    public String genTradeNo(@PathVariable("userId") String userId){
      String tradeNo=  orderService.genTradeNo(userId);
      return tradeNo;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/19 12:00
     * @description 远程调用根据id查询我们的orderInfo
     * @param orderId
     * @return com.atguigu.gmall.model.order.OrderInfo
     **/
    @RequestMapping("getOrderInfoById/{orderId}")
    public OrderInfo getOrderInfoById(@PathVariable("orderId") Long orderId){
    OrderInfo orderInfo  =  orderService.getOrderInfoById(orderId);
    return orderInfo;
    }
}

