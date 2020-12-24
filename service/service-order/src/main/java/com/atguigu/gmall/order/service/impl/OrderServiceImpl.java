package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author Kilig Zong
 * @Date 2020/12/18 12:33
 * @Version 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /***
     * @author Kilig Zong
     * @date 2020/12/18 12:34
     * @description 根据id查询我们的购物车的信息
     * @param userId
     * @return java.util.List<com.atguigu.gmall.model.order.OrderDetail>
     **/
    @Override
    public List<OrderDetail> getTrade(String userId) {
        //远程调用获取购物车的信息
      List<CartInfo> cartInfos=  cartFeignClient.cartList(userId);
      //将购物车的信息转化成我们的List<OrderDetail>
        List<OrderDetail> orderDetails=new ArrayList<>();
        for (CartInfo cartInfo : cartInfos) {
            if(cartInfo.getIsChecked()==1){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetails.add(orderDetail);
            }
        }
        return orderDetails;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/18 18:27
     * @description 我们的结算页面以及提交需要生成我们的订单的detail
     * @param order
     * @param userId
     * @return java.lang.String
     **/
    @Override
    public String submitOrder(OrderInfo order, String userId) {
        //获取并结算页面的总价格
        BigDecimal totalAmount = getTotalAmount(order.getOrderDetailList());
        order.setTotalAmount(totalAmount);
        //设置我们的进度状态
        order.setProcessStatus(ProcessStatus.UNPAID.getComment());
        order.setOrderStatus(OrderStatus.UNPAID.getComment());
        //设置用户id
        order.setUserId(Long.parseLong(userId));
        //设置订单备注
        order.setOrderComment("大勇哥来按摩了");
        //设置创建时间
        Date data = new Date();
        order.setCreateTime(data);
        //设置订单过期时间
        Calendar instance = Calendar.getInstance();
        instance.add(1,Calendar.DATE);
        order.setExpireTime(instance.getTime());
        //设置外部订单号
        String outTradeNo="atguigu";
         outTradeNo+= System.currentTimeMillis();
        SimpleDateFormat sfd = new SimpleDateFormat("yyMMddHHmmss");
        outTradeNo+= sfd.format(data);
        order.setOutTradeNo(outTradeNo);
        //设置照片的默认照片
        //待写
        // 设置订单保存数据到数据库
        orderInfoMapper.insert(order);
        //保存detail
        Long id = order.getId();
        List<OrderDetail> orderDetailList = order.getOrderDetailList();
        if(null!=orderDetailList&&orderDetailList.size()>0){
            for (OrderDetail orderDetail : orderDetailList) {
                orderDetail.setOrderId(id);
                orderDetailMapper.insert(orderDetail);
            }
        }


        return id+"";
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/18 19:34
     * @description 生成我们的唯一交易流水号防止订单重复提交
     * @param userId
     * @return java.lang.String
     **/
    @Override
    public String genTradeNo(String userId) {
        String tradeNo= UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("user:"+userId+":tradeCode",tradeNo);
        return tradeNo;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/18 19:42
     * @description 检查我们的唯一交易流水号是否有效,如果是第一次的话生成我们的唯一交易流水号，获取他并且删除
     * @param userId
     * @return boolean
     **/
    @Override
    public boolean checkTradeNo(String tradeNo,String userId) {
        String tradeNoForCache = (String)redisTemplate.opsForValue().get("user:" + userId + ":tradeCode");
        if(!StringUtils.isEmpty(tradeNoForCache)&&tradeNo.equals(tradeNoForCache)){
            redisTemplate.delete("user:" + userId + ":tradeCode");
            return true;
        }else {
            return false;
        }

    }

    /***
     * @author Kilig Zong
     * @date 2020/12/19 12:01
     * @description 根据订单id查询我们的订单
     * @param orderId
     * @return com.atguigu.gmall.model.order.OrderInfo
     **/
    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("id",orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoQueryWrapper);

        QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
        orderDetailQueryWrapper.eq("order_id",orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(orderDetailQueryWrapper);
        orderInfo.setOrderDetailList(orderDetails);
        return orderInfo;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/22 12:22
     * @description 根据我们的outtradeno对外业务订单号修改我们的订单
     * @param paymentInfo
     * @return java.lang.Long
     **/
    @Override
    public Long updateByOutTradeNo(PaymentInfo paymentInfo) {
        OrderInfo orderInfo = new OrderInfo();
        String outTradeNo = paymentInfo.getOutTradeNo();
        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        orderInfo.setTradeBody(paymentInfo.getCallbackContent());
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",outTradeNo);
        orderInfoMapper.update(orderInfo,wrapper);
        //查询我们的订单将订单号返回
        OrderInfo orderInfoFromDb = orderInfoMapper.selectOne(wrapper);
        if(null!=orderInfoFromDb){
            return orderInfoFromDb.getId();
        }else {
            return null;
        }

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
                //获得同一件商品的总价格
                BigDecimal totalPrice = orderPrice.multiply(new BigDecimal(skuNum + ""));
                totalAmount=totalAmount.add(totalPrice);
            }
        }
        return totalAmount;
    }


}
