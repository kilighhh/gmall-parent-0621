package com.atguigu.gmall.mq.receiver;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author Kilig Zong
 * @Date 2020/12/21 14:16
 * @Version 1.0
 */
@RestController
public class TestMqConsumer {

    /***
     * @author Kilig Zong
     * @date 2020/12/21 14:25
     * @description
     * @param message 消息的所有
     * @param channel 管道
     * @param str 消息
     * @return void
     **/
    @RabbitListener(bindings =@QueueBinding(
            exchange = @Exchange(value = "confirm.exchange",autoDelete = "false"),
            value = @Queue(value = "confirm.queue",autoDelete = "false"),
            key = {"confirm.routing"}))
    public  void consumerMessage( Channel channel,Message message,String str) throws IOException {
        String messageStr =message.getBody().toString();
        System.out.println("messageStr = " + messageStr);
        System.out.println(str);
        System.out.println("消费消息");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //channel.basicAck(deliveryTag,true);

    }
}
