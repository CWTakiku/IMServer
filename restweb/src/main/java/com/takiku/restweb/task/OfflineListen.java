package com.takiku.restweb.task;


import com.rabbitmq.client.Channel;
import com.takiku.restweb.service.OfflineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;
import parse.ParseService;
import po.ImConstant;

import javax.annotation.PostConstruct;

/**
 * Date: 2019-05-15
 * Time: 22:58
 *
 * @author yrw
 */
@Component
public class OfflineListen implements ChannelAwareMessageListener {
    private Logger logger = LoggerFactory.getLogger(OfflineListen.class);

    private ParseService parseService;
    private OfflineService offlineService;

    public OfflineListen(OfflineService offlineService) {
        this.parseService = new ParseService();
        this.offlineService = offlineService;
    }

    @PostConstruct
    public void init() {
        logger.info("[OfflineConsumer] Start listening Offline queue......");
    }

    @Override
    @RabbitHandler
    @RabbitListener(queues = ImConstant.MQ_OFFLINE_QUEUE, containerFactory = "listenerFactory")
    public void onMessage(Message message, Channel channel) throws Exception {
        logger.info("[OfflineConsumer] getUserSpi msg: {}", message.toString());
        try {
//
//            int code = message.getBody()[0];
//
//            byte[] msgBody = new byte[message.getBody().length - 1];
//            System.arraycopy(message.getBody(), 1, msgBody, 0, message.getBody().length - 1);

//            com.google.protobuf.Message msg = parseService.getMsgByCode(code, msgBody);
//            if (code == PackProtobuf.Pack.PackType.MSG.getNumber()) {
//                offlineService.saveChat((PackProtobuf.Msg) msg);
//            } else {
//                offlineService.saveReply((PackProtobuf.Reply) msg);
//            }
            PackProtobuf.Pack pack=PackProtobuf.Pack.parseFrom(message.getBody());
            if (pack.getPackType()==PackProtobuf.Pack.PackType.MSG){
                offlineService.saveChat(pack.getMsg());
            }else {
                offlineService.saveReply(pack.getReply());
            }

        } catch (Exception e) {
            logger.error("[OfflineConsumer] has error", e);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
