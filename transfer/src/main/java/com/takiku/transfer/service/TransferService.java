package com.takiku.transfer.service;

import com.google.protobuf.Message;
import com.rabbitmq.client.MessageProperties;
import com.takiku.transfer.domain.ConnectorConnContext;
import com.takiku.transfer.mqproducer.TransferMqProducer;
import conn.ConnectorConn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import po.ImConstant;
import protobuf.PackProtobuf;

import java.io.IOException;

@Component
public class TransferService {

    @Autowired
    private ConnectorConnContext connContext;
    @Autowired
    private TransferMqProducer producer;
    private static Logger logger = LoggerFactory.getLogger(TransferService.class);
    public void doChat(PackProtobuf.Msg msg) throws IOException {
        logger.info("doChat "+msg.getHead().getToId()+"  content "+msg.getBody());
        ConnectorConn conn = connContext.getConnByUserId(msg.getHead().getToId());

        if (conn != null) {
            conn.getCtx().writeAndFlush(msg);
        } else {
            doOffline(PackProtobuf.Pack.PackType.MSG_VALUE,msg);
        }
    }

    private void doOffline(int type,Message msg) throws IOException {
        logger.info("doOffline");
        producer.basicPublish(ImConstant.MQ_EXCHANGE, ImConstant.MQ_ROUTING_KEY,
                MessageProperties.PERSISTENT_TEXT_PLAIN, type,msg);
    }

    public void doSendAck(PackProtobuf.Ack msg) throws IOException {
//        ConnectorConn conn = connContext.getConnByUserId(msg.getAckMsgId());
//
//        if (conn != null) {
//            conn.getCtx().writeAndFlush(msg);
//        } else {
//          //  doOffline(PackProtobuf.Pack.PackType.ACK_VALUE,msg);
//        }
    }
}
