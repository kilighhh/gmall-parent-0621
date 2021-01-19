package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.mq.constant.MqConst;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Kilig Zong
 * @Date 2020/12/19 12:13
 * @Version 1.0 StatementHandler
 */
@RequestMapping("api/payment")
@RestController
public class PaymentApiController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private RabbitService rabbitService;



    /***
     * @author Kilig Zong
     * @date 2020/12/19 12:17
     * @description 跳转到支付页面
     * @param orderId
     * @return java.lang.String
     **/
    //http://api.gmall.com/api/payment/alipay/submit/19
    @RequestMapping("alipay/submit/{orderId}")
    public String alipaySubmit(@PathVariable("orderId") Long orderId){
        //获得订单信息
        OrderInfo orderInfoById = orderFeignClient.getOrderInfoById(orderId);
        //这个form表单时我们的支付宝可以根据我们的from表单生成我们的支付宝二维码
        String form=paymentService.alipaySubmit(orderInfoById);
        //同时我们需要保存我们的支付订单
        PaymentInfo paymentInfo = new PaymentInfo();
        //订单的支付状态
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.toString());
        paymentInfo.setOrderId(orderId);
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setPaymentType("在线支付");
        paymentInfo.setOutTradeNo(orderInfoById.getOutTradeNo());
        paymentInfo.setTotalAmount(orderInfoById.getTotalAmount());
        paymentInfo.setSubject(orderInfoById.getOrderDetailList().get(0).getSkuName());
        paymentService.savePaymentInfo(paymentInfo);
        //在这里我们需要创建一个延时的消息队列，间隔时间的查询我们的数据库，防止我们的网页出现意外，我们可以去监视它
        Map<String, Object> map = new HashMap<>();
        //设定我们的查询次数
        map.put("out_trade_no",orderInfoById.getOutTradeNo());
        map.put("count",5);
        rabbitService.sendDelayMessage("exchange.delay","routing.delay",JSON.toJSONString(map),10l, TimeUnit.SECONDS);
        //把我们的from表单返回回去
        return form;
    }
    //需要写两个回调函数，在我们付款成功或者失败后支付宝会过来访问这个方法
    //我们这个是内部方法不能转发到我们的静态资源上面，只能到web-all里面的一个controller上面再访问
    //return_payment_url=http://api.gmall.com/api/payment/alipay/callback/return
    /***
     * @author Kilig Zong
     * @date 2020/12/19 15:06
     * @description 回调函数是同步回调，页面同步跳转
     * @param
     * @return java.lang.String
     **/
    @RequestMapping("alipay/callback/return")
    public String callbackReturn(HttpServletRequest request){
        //通过支付宝给我们的请求地址我们可以获取很多参数
        //自定义的订单号
        String out_trade_no = request.getParameter("out_trade_no");
        //获得支付宝生成的订单号
        String trade_no = request.getParameter("trade_no");
        //获得所有的请求内容
        String callback_content = request.getQueryString();
        //创建需要修改的支付订单
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(callback_content);
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.toString());
        //修改我们的支付订单
        paymentService.update(paymentInfo);
        //我们成功支付修改订单的消息，以及后续物流系统的消息
        //把我们的对象转化成我们的json对象
        String paymentInfoToJson = JSON.toJSONString(paymentInfo);
        // 更新订单信息(更新未订单已支付),查询支付状态
        rabbitService.sendMassage("exchange.payment.pay","routing.payment.pay", paymentInfoToJson);
        return "<form action=\"http://payment.gmall.com/paySuccess.html\">\n" +
                "</form>\n" +
                "<script>\n" +
                "document.forms[0].submit();\n" +
                "</script>";
    }
    //notify_payment_url=http://xsiv7k.natappfree.cc/api/payment/alipay/callback/notify
    /***
     * @author Kilig Zong
     * @date 2020/12/19 15:05
     * @description 回调函数 异步回调，支付宝内部调用电商的openapi
     * @param
     * @return java.lang.String
     **/
    @RequestMapping("alipay/callback/notify")
    public String callbackNotify(){
        return null;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/19 22:57
     * @description 根据我们的对外业务编号查询在支付宝中交易是否被创建
     * @param out_trade_no
     * @return com.atguigu.gmall.common.result.Result
     **/
    //我们需要自己查询我们的订单在支付宝是否被创建
    @RequestMapping("alipay/query/{out_trade_no}")
    public Result query(@PathVariable("out_trade_no")String out_trade_no){
        Map<String,Object> queryMap =paymentService.query(out_trade_no);

            return Result.ok(queryMap);

    }
}
