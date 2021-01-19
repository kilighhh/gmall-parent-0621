package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.seckill.clent.SeckillFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/23 19:47
 * @Version 1.0
 */
@Controller
public class SeckillController{

    @Autowired
    private SeckillFeignClient seckillFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;

    /***
     * @author Kilig Zong
     * @date 2020/12/23 21:04
     * @description 去首页
     * @param model
     * @return java.lang.String
     **/
    @GetMapping("seckill.html")
    public  String index(Model model){
       List<SeckillGoods> seckillGoods= seckillFeignClient.findAll();
       model.addAttribute("list",seckillGoods);
       return "seckill/index";
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/23 22:23
     * @description 秒杀页商品详情
     * @param skuId
     * @param model
     * @return java.lang.String
     **/
    //http://activity.gmall.com/seckill/30.html
    @GetMapping("seckill/{skuId}.html")
    public  String getItem(@PathVariable("skuId") Long skuId,Model model){
        //通过skuId查询商品的具体商品
       SeckillGoods seckillGoods = seckillFeignClient.getItem(skuId);
       model.addAttribute("item",seckillGoods);
       return "seckill/item";
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/25 19:44
     * @description 去排队页面
     * @param model
     * @param skuId
     * @param request
     * @param skuIdStr
     * @return java.lang.String
     **/
    //http://activity.gmall.com/seckill/queue.html?skuId=30&skuIdStr=eccbc87e4b5ce2fe28308fd9f2a7baf3
    @GetMapping("seckill/queue.html")
    public String queue(Model model,Long skuId, HttpServletRequest request,String skuIdStr){
        //获得用户的id然后对比我们的抢购码
       String userId= request.getHeader("userId");
       String checkCode= MD5.encrypt(userId);
       //检查我们的抢购码
       if(null!=skuIdStr&&checkCode.equals(skuIdStr)){
           model.addAttribute("skuId",skuId);
           model.addAttribute("skuIdStr",skuIdStr);
           return "seckill/queue";
       }else {
           return "seckill/fail";
       }
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/25 19:47
     * @description 如果我们抢购成功的话就要去下单了呀
     * @param model
     * @param request
     * @return java.lang.String
     **/
    //http://activity.gmall.com/seckill/trade.html
    @RequestMapping("seckill/trade.html")
    public String trade(Model model,HttpServletRequest request){
        String userId = request.getHeader("userId");
        //获得我们秒杀商品的的详情
        OrderRecode orderRecode= seckillFeignClient.getOrderRecode(userId);
        //需要获取userAddress
        List<UserAddress> userAddressListByUserId = userFeignClient.findUserAddressListByUserId(userId);
        //order订单
        OrderInfo order = new OrderInfo();
        //收获地址
        order.setDeliveryAddress(userAddressListByUserId.get(0).toString());
        //收获人
        order.setConsignee(userAddressListByUserId.get(0).getConsignee());
        //收获人电话
        order.setConsigneeTel(userAddressListByUserId.get(0).getPhoneNum());
        //detailArrayList
        List<OrderDetail> orderDetails = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();
        SeckillGoods seckillGoods = orderRecode.getSeckillGoods();
        orderDetail.setSkuNum(seckillGoods.getNum());
        orderDetail.setOrderPrice(seckillGoods.getPrice());
        orderDetail.setImgUrl(seckillGoods.getSkuDefaultImg());
        orderDetail.setSkuName(seckillGoods.getSkuName());
        orderDetail.setSkuId(seckillGoods.getSkuId());
        orderDetails.add(orderDetail);
        order.setOrderDetailList(orderDetails);
        model.addAttribute("detailArrayList",orderDetails);

        model.addAttribute("userAddressList",userAddressListByUserId);

        model.addAttribute("order",order);
        model.addAttribute("totalAmount",seckillGoods.getPrice());
        return "seckill/trade";
    }
}
