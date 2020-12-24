package com.atguigu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.model.ware.WareOrderTask;
import com.atguigu.gmall.model.ware.WareOrderTaskDetail;
import com.atguigu.gmall.mq.constant.MqConst;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/22 12:06
 * @Version 1.0
 */
@Component
public class OrderConsumer {

    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private OrderService orderService;


    /***
     * @author Kilig Zong
     * @date 2020/12/22 12:16
     * @description 接收rabbitmq的信息，更改我们的订单的数据库，记住要创建Channel
     * @param channel
     * @param message
     * @param paymentInfoToJson
     * @return void
     **/
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "exchange.payment.pay",autoDelete = "false"),
            value = @Queue(value = "queue.payment.pay",autoDelete = "false"),
            key = {"routing.payment.pay"}
    ))
    public void orderConsumer(Channel channel, Message message,String paymentInfoToJson)throws IOException {
        //获得我们的json对象转换成我们需要的对象
        PaymentInfo paymentInfo = JSON.parseObject(paymentInfoToJson, PaymentInfo.class);
        //修改我们订单的信息
        Long orderId=orderService.updateByOutTradeNo(paymentInfo);
        //判断我们是否拿到我们的orderId
        if(null!=orderId&&orderId>0){
            // 给库存发通知，锁定商品
            OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
            List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
            //把我们的orderInfo转发成我们仓库的bean
            WareOrderTask wareOrderTask = new WareOrderTask();
            wareOrderTask.setDeliveryAddress(orderInfo.getDeliveryAddress());
            wareOrderTask.setPaymentWay(orderInfo.getPaymentWay());
            wareOrderTask.setCreateTime(new Date());
            wareOrderTask.setConsigneeTel(orderInfo.getConsigneeTel());
            wareOrderTask.setConsignee(orderInfo.getConsignee());
            wareOrderTask.setOrderId(orderId+"");
            List<WareOrderTaskDetail> wareOrderTaskDetails = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetailList) {
                WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();

                wareOrderTaskDetail.setSkuName(orderDetail.getSkuName());
                wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
                wareOrderTaskDetail.setSkuId(orderDetail.getSkuId()+"");
                wareOrderTaskDetails.add(wareOrderTaskDetail);
            }
            wareOrderTask.setDetails(wareOrderTaskDetails);

            rabbitService.sendMassage("exchange.direct.ware.stock","ware.stock",JSON.toJSONString(wareOrderTask));

        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }


}
