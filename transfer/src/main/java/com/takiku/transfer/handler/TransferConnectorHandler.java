package com.takiku.transfer.handler;

import com.google.protobuf.Message;
import com.takiku.transfer.config.SpringUtil;
import com.takiku.transfer.service.TransferService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import parse.AbstractPackParser;
import protobuf.PackProtobuf;

@ChannelHandler.Sharable
@Component
public class TransferConnectorHandler extends SimpleChannelInboundHandler<Message> {

    private Logger logger = LoggerFactory.getLogger(TransferConnectorHandler.class);
     private FromConnectorParser fromConnectorParser;
    private static TransferService transferService;

    public TransferConnectorHandler(){
        fromConnectorParser = new FromConnectorParser();
    }

    {
        transferService = SpringUtil.getBean(TransferService.class);
    }
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) {
        PackProtobuf.Pack pack = (PackProtobuf.Pack) message;
        switch (pack.getPackType()) {
            case MSG:
                fromConnectorParser.parse(pack.getMsg(), channelHandlerContext);
                break;
            case REPLY:
                fromConnectorParser.parse(pack.getReply(), channelHandlerContext);
                break;
            case ACK:
                fromConnectorParser.parse(pack.getAck(), channelHandlerContext);
                break;
        }

    }
    class FromConnectorParser extends AbstractPackParser {

        @Override
        public void registerParsers() {

            register(PackProtobuf.Msg.class, ((m, channelHandlerContext) -> transferService.doChat(m)));
            register(PackProtobuf.Ack.class, ((m, channelHandlerContext) -> {
                transferService.doSendAck(m);
            }));
        }
    }
}
