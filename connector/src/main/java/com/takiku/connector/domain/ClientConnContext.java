package com.takiku.connector.domain;


import com.google.inject.Singleton;
import conn.MemoryConnContext;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 保存客户端连接容器
 * Date: 2019-05-02
 * Time: 07:55
 *
 * @author yrw
 */
@Component
@Singleton
public class ClientConnContext extends MemoryConnContext<ClientConn> {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnContext.class);

    private ConcurrentMap<String, Serializable> userIdToNetId;
    private ConcurrentMap<ChannelHandlerContext,Serializable> ctxToNetId;

    public ClientConnContext() {
        this.connMap = new ConcurrentHashMap<>();
        this.userIdToNetId = new ConcurrentHashMap<>();
        this.ctxToNetId = new ConcurrentHashMap<>();
    }

    public ClientConn getConnByUserId(String userId) {
        logger.info("[get conn on this machine] userId: {}", userId + " userIdToNetId size " + userIdToNetId.size());

        Serializable netId = userIdToNetId.get(userId);
        if (netId == null) {
            logger.debug("[get conn this machine] netId not found");
            return null;
        }
        ClientConn conn = connMap.get(netId);
        if (conn == null) {
            logger.info("[get conn this machine] conn not found");
            userIdToNetId.remove(userId);
        } else {
            logger.info("[get conn this machine] found conn, userId:{}, connId: {}", userId, conn.getNetId());
        }
        return conn;
    }
    public ClientConn getConnByCtx(ChannelHandlerContext ctx){
        Serializable netId = ctxToNetId.get(ctx);
        if (netId == null) {
            logger.debug("[get conn this machine] netId not found getConnByCtx ");
            return null;
        }
        ClientConn conn = connMap.get(netId);
        if (conn == null) {
            logger.info("[get conn this machine] conn not found");
            ctxToNetId.remove(ctx);
        } else {
            logger.info("[get conn this machine] found conn, getConnByCtx, connId: {}",  conn.getNetId());
        }
        return conn;
    }

    @Override
    public void addConn(ClientConn conn) {
        String userId = conn.getUserId();

        if (userIdToNetId.containsKey(userId)) {
            removeConn(userIdToNetId.containsKey(userId));
        }
        logger.debug("[add conn on this machine] user: {} is online, netId {}", userId, conn.getNetId());

        connMap.putIfAbsent(conn.getNetId(), conn);
        userIdToNetId.put(conn.getUserId(), conn.getNetId());
        ctxToNetId.put(conn.getCtx(),conn.getNetId());

    }
}
