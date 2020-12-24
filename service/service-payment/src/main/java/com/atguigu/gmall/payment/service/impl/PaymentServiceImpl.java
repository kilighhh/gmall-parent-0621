package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.comfig.AlipayConfig;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/19 12:20
 * @Version 1.0
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;
    @Autowired//这个是支付宝的客户端，里面封装了各种我们应该封装的key
    private AlipayClient alipayClient;
    /***
     * @author Kilig Zong
     * @date 2020/12/19 12:25
     * @description 提交订单，支付宝跳转，提供一个支付二维码
     * @param orderInfoById
     * @return java.lang.String
     **/
    @Override
    public String alipaySubmit(OrderInfo orderInfoById) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //我们需要设置两个回调函数
        request.setNotifyUrl(AlipayConfig.notify_payment_url);
        request.setReturnUrl(AlipayConfig.return_payment_url);
        Map<String, Object> map = new HashMap<>();
        //这里是必选的参数
        map.put("out_trade_no",orderInfoById.getOutTradeNo());//这个
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",0.01);//这里实际上是使用实际的总价格
        map.put("subject",orderInfoById.getOrderDetailList().get(0).getSkuName());//一项商品的名字
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradePagePayResponse response = null;
        try {
            //客户端需要把请求封装
            response = alipayClient.pageExecute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //获取需提交的form表单
        String submitFormData = response.getBody();
    //客户端拿到submitFormData做表单提交
        return submitFormData;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/19 14:07
     * @description  保存我们的paymentInfo信息
     * @param paymentInfo
     * @return void
     **/
    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insert(paymentInfo);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/19 22:21
     * @description 修改我们的支付订单
     * @param paymentInfo
     * @return void
     **/
    @Override
    public void update(PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("out_trade_no",paymentInfo.getOutTradeNo());
        paymentInfoMapper.update(paymentInfo,paymentInfoQueryWrapper);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/19 23:00
     * @description 根据对外业务编号查询支付宝我们的订单是否创建
     * @param out_trade_no
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @Override
    public Map<String, Object> query(String out_trade_no) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> map = new HashMap<>();
        //请求参数封装成一个map
        map.put("out_trade_no",out_trade_no);
        String requestData = JSON.toJSONString(map);
        request.setBizContent(requestData);
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //判断是否创建成功
        boolean flag = response.isSuccess();
        if(flag){
            System.out.println("调用成功");
            //返回我们创建成功的状态
            map.put("trade_status",response.getTradeStatus());
            map.put("trade_no",response.getTradeNo());
            map.put("success",true);
        } else {
            System.out.println("调用失败");
            map.put("success",false);
        }
        return map;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/22 20:05
     * @description 根据对外业务编号查询支付宝我们的订单是否创建
     * @param out_trade_no
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @Override
    public Map<String, Object> checkPayment(String out_trade_no) {
        Map<String, Object> checkMap = query(out_trade_no);
        return checkMap;
    }
}
