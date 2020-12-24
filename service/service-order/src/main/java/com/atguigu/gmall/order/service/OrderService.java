package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/18 12:32
 * @Version 1.0
 */
public interface OrderService {
    List<OrderDetail> getTrade(String userId);


    String submitOrder(OrderInfo order, String userId);

    String genTradeNo(String userId);

    boolean checkTradeNo(String tradeNo,String userId);

    OrderInfo getOrderInfoById(Long orderId);

    Long updateByOutTradeNo(PaymentInfo paymentInfo);

}
