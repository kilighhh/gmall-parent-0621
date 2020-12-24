package com.atguigu.gmall.payment.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.payment.service.PaymentService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Kilig Zong
 * @Date 2020/12/22 19:19
 * @Version 1.0
 */
@Component
public class PaymentConsumer{
    @Autowired
   private PaymentService paymentService;
    @Autowired
    private RabbitService rabbitService;

    /***
     * @author Kilig Zong
     * @date 2020/12/22 19:41
     * @description 在这里监听我们前面发的验证信息
     * @param channel
     * @param message
     * @param messageStr
     * @return void
     **/
    @SneakyThrows
    @RabbitListener(queues = "queue.delay.1")
    public void paymentConsumer(Channel channel, Message message,String messageStr){
    //获得我们传输的数据
        Map<String, Object> map = new HashMap<>();
        Map<String,Object> jsonMap= JSON.parseObject(messageStr,map.getClass());
        //先获取我们存在消息里面的数据 获得我们外部的订单号
        String out_trade_no = (String)jsonMap.get("out_trade_no");
        Integer count = (Integer)jsonMap.get("count");
        System.out.println("剩余检查的次数="+count);
        //先检查我们的数据库中的数据是否有变化
       Map<String,Object> checkMap= paymentService.checkPayment(out_trade_no);
       count--;
        Boolean success = (Boolean)checkMap.get("success");
        String trade_status = (String)checkMap.get("trade_status");
        //这里我们根据我们次数还有是否成功来决定我们是否再次查询
        if(count>0){
            if(success==false&&trade_status.equals("WAIT_BUYER_PAY")){
                jsonMap.put("count",count);
                System.out.println("当前交易状态为："+trade_status+"继续检查");
                rabbitService.sendDelayMessage("exchange.delay","routing.delay",JSON.toJSONString(jsonMap),20l, TimeUnit.SECONDS);
            }else {
                System.out.println("当前交易状态为："+trade_status+"不在继续检查");
                String status="未支付";
                //这里是为了保持页面的一致性
                if(!status.equals("PAID")){
                    //就是说如果是确实支付过就不修改，防止两个请求分别修改
                }
            }
        }else {
            System.out.println("次数耗尽，停止检查");
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
