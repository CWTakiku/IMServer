package com.takiku.transfer.mqproducer;

import com.google.protobuf.GeneratedMessageV3;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.ImConstant;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TransferMqProducer {
    private static Logger logger = LoggerFactory.getLogger(TransferMqProducer.class);

    private Channel channel;

    public TransferMqProducer(String host, int port, String username, String password)
            throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(ImConstant.MQ_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, null);
        channel.queueDeclare(ImConstant.MQ_OFFLINE_QUEUE, true, false, false, null);
        channel.queueBind(ImConstant.MQ_OFFLINE_QUEUE, ImConstant.MQ_EXCHANGE, ImConstant.MQ_ROUTING_KEY);

        this.channel = channel;
        logger.info("[transfer] producer start success");
    }

    public void basicPublish(String exchange, String routingKey, AMQP.BasicProperties properties, GeneratedMessageV3 message) throws IOException {
//        PackProtobuf.Pack pack= (PackProtobuf.Pack) message;
//
//        byte[] srcB = message.toByteArray();
//        byte[] destB = new byte[srcB.length + 1];
//        destB[0] = (byte) code;
//
//        System.arraycopy(message.toByteArray(), 0, destB, 1, message.toByteArray().length);

        channel.basicPublish(exchange, routingKey, properties, message.toByteArray());
    }

    public Channel getChannel() {
        return channel;
    }
}
