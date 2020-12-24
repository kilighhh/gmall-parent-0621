package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/19 12:20
 * @Version 1.0
 */
public interface PaymentService {
    String alipaySubmit(OrderInfo orderInfoById);

    void savePaymentInfo(PaymentInfo paymentInfo);

    void update(PaymentInfo paymentInfo);

    Map<String, Object> query(String out_trade_no);

    Map<String, Object> checkPayment(String out_trade_no);
}
