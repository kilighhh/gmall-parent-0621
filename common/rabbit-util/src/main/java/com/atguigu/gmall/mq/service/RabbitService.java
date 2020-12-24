package com.atguigu.gmall.mq.service;

import java.util.concurrent.TimeUnit;

/**
 * @Author Kilig Zong
 * @Date 2020/12/21 12:16
 * @Version 1.0
 */
public interface RabbitService {
    void sendMassage(String exchange, String routingKey, Object message);


    void sendDeadMessage(String exchange_dead, String routing_1, String messageStr, long l, TimeUnit seconds);

    void sendDelayMessage(String s, String s1, String messageStr, long l, TimeUnit seconds);

}
