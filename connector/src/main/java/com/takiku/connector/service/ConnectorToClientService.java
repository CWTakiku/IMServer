package com.takiku.connector.service;

import com.google.protobuf.Message;
import com.takiku.connector.domain.ClientConnContext;
import com.takiku.connector.handler.ConnectorTransferHandler;
import conn.Conn;
import domain.ack.ServerAckWindow;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import protobuf.PackProtobuf;
import util.WrapWriter;

import java.io.Serializable;
import java.util.function.Function;

/**
 * process msg the connector received,
 * if send to client, need change msg id.
 * Date: 2019-04-08
 * Time: 21:05
 *
 * @author yrw
 */

@Service
public class ConnectorToClientService {

    private static Logger logger = LoggerFactory.getLogger(ConnectorToClientService.class);

    @Autowired
    private ClientConnContext clientConnContext;

    public void doChatToClientOrTransferAndFlush(PackProtobuf.Msg msg) {
        sendMsg(msg.getHead().getToId(), msg.getSerial(), cid -> msg);
    }

    public void doReplyToClientOrTransferAndFlush(PackProtobuf.Reply reply) {
        logger.info("doReplyToClientOrTransferAndFlush");
        sendMsg(reply.getUserId(), reply.getSerial(), cid -> reply);
    }

    public void doShakeHandsToClientAndFlush(PackProtobuf.Reply reply) {

    }

    public boolean sendMsg(String destId, Long serialId, Function<Serializable, Message> generateMsg) {
        logger.info("sendMsg");
        Conn conn = clientConnContext.getConnByUserId(destId);
        if (conn == null) {
            ChannelHandlerContext ctx = ConnectorTransferHandler.getOneOfTransferCtx(System.currentTimeMillis());
            ctx.writeAndFlush(generateMsg.apply(ctx.channel().attr(Conn.NET_ID).get()));
            return false;
        } else {
            //the user is connected to this machine
            //won 't save chat histories
            Message message = generateMsg.apply(conn.getNetId());
            ServerAckWindow.offer(conn.getNetId(), serialId, message, m -> WrapWriter.writeMsg(conn.getCtx(), m));
            return true;
        }
    }
}
