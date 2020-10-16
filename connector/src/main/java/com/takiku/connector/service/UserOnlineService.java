package com.takiku.connector.service;


import com.google.protobuf.Message;
import com.takiku.connector.domain.ClientConn;
import com.takiku.connector.domain.ClientConnContext;
import com.takiku.connector.handler.ConnectorTransferHandler;
import com.takiku.userstatus.service.UserStatusService;
import com.takiku.connector.service.impl.RedisUserStatusServiceImpl;
import domain.ack.ServerAckWindow;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import protobuf.PackProtobuf;
import util.WrapWriter;

import java.time.Duration;
import java.util.List;

/**
 * 用户上线、下线服务
 */
@Service
public class UserOnlineService {
    @Autowired
    private ClientConnContext clientConnContext;
    @Autowired
    private ConnectorToClientService connectorToClientService;
    @Autowired
    private OfflineService offlineService;


    private UserStatusService userStatusService;

    private ServerAckWindow serverAckWindow;

    public ServerAckWindow getServerAckWindow() {
        return serverAckWindow;
    }

    public UserOnlineService() {
        userStatusService = new RedisUserStatusServiceImpl();
    }

    /**
     * 用户上线，将取出该用户的离线消息发送给他
     *
     * @param userId
     * @param ctx
     * @return
     */
    public ClientConn userOnline(String userId,String token, ChannelHandlerContext ctx) {
        //get all offline msg and send
        ClientConn conn = new ClientConn(ctx);
        serverAckWindow = new ServerAckWindow(conn.getNetId(), 10, Duration.ofSeconds(5));
        List<Message> msgs = offlineService.pollOfflineMsg(userId,token);
        if (msgs != null) {
            msgs.forEach(msg -> {
                try {
                    PackProtobuf.Msg chatMsg = (PackProtobuf.Msg) msg;
                    ServerAckWindow.offer(conn.getNetId(), ((PackProtobuf.Msg) msg).getSerial(), chatMsg, m -> WrapWriter.writeMsg(conn.getCtx(), m));
                  //  WrapWriter.writeMsg(ctx,chatMsg);
                  //  connectorToClientService.doChatToClientOrTransferAndFlush(chatMsg);
                } catch (ClassCastException ex) {
                }
            });

        }
        //save connection

        conn.setUserId(userId);
        clientConnContext.addConn(conn);

        //user is online
        String oldConnectorId = userStatusService.online(userId, ConnectorTransferHandler.CONNECTOR_ID);
        if (oldConnectorId != null) {
            //can't online twice
            //sendErrorToClient("already online", ctx);
        }

        return conn;
    }

    /**
     * 用户下线
     *
     * @param ctx
     */
    public void userOffline(ChannelHandlerContext ctx) {
        ClientConn conn = clientConnContext.getConn(ctx);
        if (conn == null) {
            return;
        }
        userStatusService.offline(conn.getUserId());
        //remove the connection
        clientConnContext.removeConn(ctx);
    }
}
