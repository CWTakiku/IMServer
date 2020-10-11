package util;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.PackProtobuf;

import static internal.Constants.HEART_ACK_TYPE;
import static internal.Constants.MSG_RESULT_OK;

public class WrapWriter {
    private static Logger logger = LoggerFactory.getLogger(WrapWriter.class);

    public static void writeMsg(ChannelHandlerContext ctx, Message message) {
        if (ctx == null) {
            return;
        }
        PackProtobuf.Pack pack = null;
        if (message instanceof PackProtobuf.Msg) {
            logger.info("msg");
            pack = PackProtobuf.Pack.newBuilder()
                    .setPackType(PackProtobuf.Pack.PackType.MSG)
                    .setMsg((PackProtobuf.Msg) message)
                    .build();
        } else if (message instanceof PackProtobuf.Reply) {
            logger.info("Reply");
            pack = PackProtobuf.Pack.newBuilder()
                    .setPackType(PackProtobuf.Pack.PackType.REPLY)
                    .setReply((PackProtobuf.Reply) message)
                    .build();
        }
        ctx.writeAndFlush(pack);
    }

    public static void writeAck(ChannelHandlerContext ctx, String msgId, int ackType, int result) {
        PackProtobuf.Pack pack = PackProtobuf.Pack.newBuilder()
                .setPackType(PackProtobuf.Pack.PackType.ACK)
                .setAck(PackProtobuf.Ack.newBuilder()
                        .setAckType(ackType).setAckMsgId(msgId).setResult(result).build())
                .build();
        ctx.writeAndFlush(pack);
    }

    public static void writeAckHeart(ChannelHandlerContext ctx, PackProtobuf.Heart heart) {
        PackProtobuf.Pack pack = PackProtobuf.Pack.newBuilder()
                .setPackType(PackProtobuf.Pack.PackType.ACK)
                .setAck(PackProtobuf.Ack.newBuilder()
                        .setAckType(HEART_ACK_TYPE)
                        .setResult(MSG_RESULT_OK))
                .build();
        ctx.writeAndFlush(pack);
    }
}
