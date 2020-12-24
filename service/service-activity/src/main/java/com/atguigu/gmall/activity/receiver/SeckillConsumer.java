package com.atguigu.gmall.activity.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.activity.UserRecode;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author Kilig Zong
 * @Date 2020/12/23 23:04
 * @Version 1.0
 */
@Component
public class SeckillConsumer {

    @Autowired
    private RedisTemplate redisTemplate;


    /***
     * @author Kilig Zong
     * @date 2020/12/23 23:28
     * @description 准备下单，先查看我们是否还有库存，再进行下单
     * @param channel
     * @param message
     * @param json
     * @return void
     **/
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange =@Exchange(value = "exchange.direct.seckill.user", autoDelete = "false"),
            value =@Queue(value = "queue.seckill.user",autoDelete = "false"),
            key = {"seckill.user"}
    ))
    public void seckillConsume(Channel channel, Message message,String json) throws IOException {
        //先获取我们的消息的本体
        UserRecode userRecode = JSON.parseObject(json, UserRecode.class);
        //先查看我们的库存是否还有
        Object stock = redisTemplate.opsForList().rightPop("seckill:stock:" + userRecode.getSkuId());
        if(null==stock){
        //如果为空则需要通知各个微服务，不能再抢购
            redisTemplate.convertAndSend("seckillpush",userRecode.getSkuId()+":0");
        }else {
            //如果不为空抢购，并且生成预购单
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
