package com.atguigu.gmall.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author Kilig Zong
 * @Date 2020/12/21 12:20
 * @Version 1.0
 */
@Component
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ReturnCallback,RabbitTemplate.ConfirmCallback{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setConfirmCallback(this);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/21 12:26
     * @description
     * @param correlationData 相关性数据
     * @param ack  rabbitmq交换机是否收到消息
     * @param cause 未收到消息的原因
     * @return void
     **/
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id=correlationData!=null?correlationData.getId():"";
        if(ack){
            log.info("交换机已经收到id:{}消息",id);
        }else{
            log.info("交换机未收到id:{}消息，原因是:{}",id,cause);
        }
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/21 12:26
     * @description  回退方法 消息无法路由调用该方法
     * @param message
     * @param callBackFlag
     * @param replyText
     * @param exchange
     * @param routingKey
     * @return void
     **/
    @Override
    public void returnedMessage(Message message, int callBackFlag, String replyText,  String exchange, String routingKey) {
        log.info("消息:{},被交换机{}退回,退回原因:{},路由key:{}",new String(message.getBody()),exchange,replyText,routingKey);
    }
}
