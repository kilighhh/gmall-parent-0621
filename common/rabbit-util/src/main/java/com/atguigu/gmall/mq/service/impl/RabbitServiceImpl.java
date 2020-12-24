package com.atguigu.gmall.mq.service.impl;

import com.atguigu.gmall.mq.service.RabbitService;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author Kilig Zong
 * @Date 2020/12/21 12:17
 * @Version 1.0
 */
@Service
public class RabbitServiceImpl implements RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendMassage(String exchange, String routingKey, Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }

    @Override
    public void sendDeadMessage(String exchange_dead, String routing_1, String messageStr, long ttl, TimeUnit seconds) {
        System.out.println("收到的消息是="+messageStr);
        rabbitTemplate.convertAndSend(exchange_dead,routing_1,messageStr, messagePostProcessor->{
            //设置消息队列的消息对象他的过期时间
            messagePostProcessor.getMessageProperties().setExpiration(ttl*1000+"");
            byte[] body = messagePostProcessor.getBody();
            String str = new String(body);
            System.out.println(str);
            return messagePostProcessor;
        });
    }
    @Override
    public void sendDelayMessage(String exchange_dead, String routing_1, String messageStr, long ttl, TimeUnit seconds) {
        // 用户发的消息messageStr
        System.out.println(messageStr);
        rabbitTemplate.convertAndSend(exchange_dead,routing_1,messageStr, messagePostProcessor ->{
            // 设置消息队列的消息对象
            messagePostProcessor.getMessageProperties().setDelay(Integer.parseInt(ttl+"")*1000);
            byte[] body = messagePostProcessor.getBody();
            String str = new String(body);
            return messagePostProcessor;
        });
    }


}
