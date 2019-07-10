package com.ximen.rabbitmq.fanout;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues="itcast")
public class ConsumerOne {
    @RabbitHandler
    public void getQueuesMessage(String message){
        System.out.println("itcast：" + message);
    }
}
