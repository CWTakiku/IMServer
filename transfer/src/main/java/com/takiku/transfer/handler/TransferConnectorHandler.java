package com.takiku.transfer.handler;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TransferConnectorHandler extends SimpleChannelInboundHandler<Message> {
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {

    }
}
