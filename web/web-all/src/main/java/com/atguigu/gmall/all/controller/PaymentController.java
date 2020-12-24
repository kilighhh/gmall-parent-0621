package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Kilig Zong
 * @Date 2020/12/19 11:56
 * @Version 1.0
 */
@Controller
public class PaymentController {
    @Autowired
    private OrderFeignClient orderFeignClient;
    /***
     * @author Kilig Zong
     * @date 2020/12/19 14:30
     * @description 在结算页面跳转到我们的支付页面
     * @param orderId
     * @param model
     * @return java.lang.String
     **/
    //http://payment.gmall.com/pay.html?orderId=19
    @RequestMapping("pay.html")
    public String goToPay(Long orderId, Model model){
       OrderInfo orderInfo =orderFeignClient.getOrderInfoById(orderId);
        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/19 14:37
     * @description 我们支付成功后回调函数会自动访问这个方法，因为我们在支付的api的request里面设置了
     * @param
     * @return java.lang.String
     **/
    //http://payment.gmall.com/paySuccess.html
    @RequestMapping("paySuccess.html")
    public String paySuccess(HttpServletRequest request){

        return "payment/success";
    }
}
