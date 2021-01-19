package com.atguigu.gmall.order.client;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/18 12:05
 * @Version 1.0
 */
@FeignClient(value = "service-order")
public interface OrderFeignClient {
    @RequestMapping("/api/order/getTrade")
    List<OrderDetail> getTrade();
    @RequestMapping("/api/order/genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId") String userId);
    @RequestMapping("/api/order/getOrderInfoById/{orderId}")
    OrderInfo getOrderInfoById(@PathVariable("orderId") Long orderId);
    @RequestMapping("/api/order/submitOrder")
    String submitOrder(@RequestBody OrderInfo order);
}
