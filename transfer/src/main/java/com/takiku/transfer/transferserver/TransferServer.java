package com.takiku.transfer.transferserver;

import code.MsgDecoder;
import code.MsgEncoder;
import com.takiku.transfer.handler.TransferConnectorHandler;
import exception.IMException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TransferServer {
    private static Logger logger = LoggerFactory.getLogger(TransferServer.class);
    public void startTransferServer(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("MsgDecoder", new MsgDecoder(PackProtobuf.Pack.getDefaultInstance()));
                        pipeline.addLast("MsgEncoder", new MsgEncoder());
                        pipeline.addLast("TransferClientHandler", new TransferConnectorHandler());
                    }
                });

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("[transfer] start successful at port {}, waiting for connectors to connect...", port);
            } else {
                throw new IMException("[transfer] start failed");
            }
        });

        try {
            f.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new IMException("[transfer] start failed");
        }
    }
}
