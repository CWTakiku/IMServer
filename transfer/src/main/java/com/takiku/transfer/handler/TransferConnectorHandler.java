package com.takiku.transfer.handler;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferConnectorHandler extends SimpleChannelInboundHandler<Message> {

    private Logger logger = LoggerFactory.getLogger(TransferConnectorHandler.class);

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) {


    }
}
