package com.atguigu.gmall.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeadLetterMqConfig {

    public static final String exchange_dead = "exchange.dead";
    public static final String routing_1 = "routing.1";
    public static final String routing_dead_2 = "routing.dead.2";
    public static final String queue_1 = "queue.1";
    public static final String queue_2 = "queue.2";

    /**
     * 其他队列可以在RabbitListener上面做绑定
     *
     * @return
     */

    @Bean
    public DirectExchange exchange() {

        return new DirectExchange(exchange_dead, true, false, null);
    }

    @Bean
    public Queue queue1() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", exchange_dead);// 死信息交换机
        arguments.put("x-dead-letter-routing-key", routing_dead_2);// 死信路由
        return new Queue(queue_1, true, false, false, arguments);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue1()).to(exchange()).with(routing_1);
    }

    @Bean
    public Queue queue2() {
        return new Queue(queue_2, true, false, false, null);
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(queue2()).to(exchange()).with(routing_dead_2);
    }
}
