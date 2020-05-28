package com.takiku.restweb.config;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import po.ImConstant;

/**
 * Date: 2019-04-21
 * Time: 15:08
 *
 * @author yrw
 */
@Configuration
public class RestConfig {




    @Bean
    public SimpleRabbitListenerContainerFactory listenerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public Queue offlineQueue() {
        return new Queue(ImConstant.MQ_OFFLINE_QUEUE);
    }


}
