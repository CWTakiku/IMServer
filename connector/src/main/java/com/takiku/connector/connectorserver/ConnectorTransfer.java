package com.takiku.connector.connectorserver;

import code.MsgDecoder;
import code.MsgEncoder;
import com.takiku.connector.handler.ConnectorTransferHandler;
import exception.IMException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import protobuf.PackProtobuf;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConnectorTransfer {

    public void start(String transferUrl) {

        String[] url = transferUrl.split(":");

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        ChannelFuture f = b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("MsgDecoder", new MsgDecoder(PackProtobuf.Pack.getDefaultInstance()));
                        p.addLast("MsgEncoder", new MsgEncoder());
                        p.addLast("ConnectorTransferHandler", new ConnectorTransferHandler());
                    }
                }).connect(url[0], Integer.parseInt(url[1]))
                .addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        throw new IMException("[connector] connect to transfer failed! transfer url: " + transferUrl);
                    }
                });

        try {
            f.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new IMException("[connector] connect to transfer failed! transfer url: " + transferUrl, e);
        }
    }
}

