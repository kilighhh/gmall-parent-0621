package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.mq.config.DeadLetterMqConfig;
import com.atguigu.gmall.mq.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author Kilig Zong
 * @Date 2020/12/21 14:11
 * @Version 1.0
 */
@RestController
public class TestMqProducer {

    @Autowired
    private RabbitService rabbitService;

    @RequestMapping("api/mq/testSendMassage/{message}")
    public Result testSendMassage(@PathVariable("message") String message){
        rabbitService.sendMassage("confirm.exchange","confirm.routing",message);
        return Result.ok();
    }
    @RequestMapping("api/mq/testSendDeadMessage/{messageStr}")//死信队列
    public  Result testSendDeadMessage(@PathVariable("messageStr") String messageStr){
        //发送死信消息
        rabbitService.sendDeadMessage(DeadLetterMqConfig.exchange_dead,DeadLetterMqConfig.routing_1,messageStr,15l, TimeUnit.SECONDS);
            return Result.ok();
    }
    @RequestMapping("api/mq/testSendDelayMessage/{messageStr}")
    public Result sendDelayMessage(@PathVariable("messageStr") String messageStr){
        // 发送死信消息
        rabbitService.sendDelayMessage("exchange.delay","routing.delay",messageStr,10l, TimeUnit.SECONDS);
        return Result.ok();
    }


}
