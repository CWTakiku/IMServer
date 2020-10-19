package com.takiku.connector.connectorserver;

import code.MsgDecoder;
import code.MsgEncoder;
import com.takiku.connector.exception.ImException;
import com.takiku.connector.handler.ConnectorClientHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.PackProtobuf;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ConnectorServer {
    private static final Logger logger = LoggerFactory.getLogger(ConnectorServer.class);


    private volatile boolean isRunning = false;

    private String port;


    public ConnectorServer(String port) {
        this.port = port;
    }

    private void bind() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535,
                                0, 2, 0, 2));
                        pipeline.addLast("MsgDecoder", new MsgDecoder(PackProtobuf.Pack.getDefaultInstance()));
                        pipeline.addLast("MsgEncoder", new MsgEncoder());
                        pipeline.addLast("ConnectorServerHandler", new ConnectorClientHandler());
                    }
                });

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(Integer.valueOf(port))).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("[connector] start successfully at port {}, waiting for clients to connect...", port);
            } else {
                throw new ImException("[connector] start failed");
            }
        });
        try {
            f.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new ImException("[connector] start failed", e);
        }
    }

    public void startServer() {
        if (this.isRunning) {
            throw new IllegalStateException(this.getName() + " is already started .");
        }
        this.isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                bind();
            }
        }, getName()).start();
    }

    private String getName() {
        return "Connector-Server";
    }
}
