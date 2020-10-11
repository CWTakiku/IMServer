package domain.ack;


import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import protobuf.PackProtobuf;
import util.IdWorker;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static internal.Constants.MSG_ACK_TYPE;
import static internal.Constants.MSG_RESULT_OK;

/**
 * msg need to be processed from server
 * Date: 2019-09-08
 * Time: 20:55
 *
 * @author yrw
 */
public class ProcessMsgNode {

    private String id;
    private Long serial;

    private ChannelHandlerContext ctx;

    private CompletableFuture<Void> future;

    private Message message;

    private Consumer<Message> consumer;

    public ProcessMsgNode(String id, Long serial,
                          ChannelHandlerContext ctx, Message message, Consumer<Message> consumer) {
        this.id = id;
        this.serial = serial;
        this.ctx = ctx;
        this.message = message;
        this.consumer = consumer;
        this.future = new CompletableFuture<>();
    }

    public Void process() {
        consumer.accept(message);
        return null;
    }

    public void sendAck() {
        if (ctx.channel().isOpen()) {
//            Internal.InternalMsg ack = Internal.InternalMsg.newBuilder()
//                .setVersion(MsgVersion.V1.getVersion())
//                .setId(IdWorker.snowGenId())
//                .setFrom(from)
//                .setDest(dest)
//                .setCreateTime(System.currentTimeMillis())
//                .setMsgType(Internal.InternalMsg.MsgType.ACK)
//                .setMsgBody(id + "")
//                .build();
            PackProtobuf.Ack ack = PackProtobuf.Ack.newBuilder()
                    .setAckMsgId(id)
                    .setAckType(MSG_ACK_TYPE)
                    .setResult(MSG_RESULT_OK)
                    .build();

            ctx.writeAndFlush(ack);
        }
    }

    public void complete() {
        this.future.complete(null);
    }

    public CompletableFuture<Void> getFuture() {
        return future;
    }

    public String getId() {
        return id;
    }

    public Long getSerial() {
        return serial;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
}
