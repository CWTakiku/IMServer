package domain.ack;


import com.google.protobuf.Message;
import exception.IMException;
import internal.Constants;
import internal.InternalAck;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * for client, every connection should has an ServerAckWindow
 * Date: 2019-09-08
 * Time: 20:42
 *
 * @author yrw
 */
public class ClientAckWindow {
    private static Logger logger = LoggerFactory.getLogger(ClientAckWindow.class);

    private final int maxSize;

    private AtomicBoolean first;
    private AtomicLong lastId;
    private ConcurrentMap<Long, ProcessMsgNode> notContinuousMap;

    public ClientAckWindow(int maxSize) {
        this.first = new AtomicBoolean(true);
        this.maxSize = maxSize;
        this.lastId = new AtomicLong(-1);
        this.notContinuousMap = new ConcurrentHashMap<>();
    }

    /**
     * multi thread do it
     *
     * @param receivedMsg
     * @param processFunction
     */
    public CompletableFuture<Void> offer(String msgId,Long serial,
                                         ChannelHandlerContext ctx, Message receivedMsg, Consumer<Message> processFunction) {
        if (isRepeat(serial)) {
            ctx.writeAndFlush(InternalAck.createAck(msgId, Constants.MSG_ACK_TYPE,Constants.MSG_RESULT_OK));
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.complete(null);
            return future;
        }

        ProcessMsgNode msgNode = new ProcessMsgNode(msgId, serial, ctx, receivedMsg, processFunction);
        if (!isContinuous(serial)) {
            if (notContinuousMap.size() >= maxSize) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                future.completeExceptionally(new IMException("client window is full"));
                return future;
            }
            notContinuousMap.put(serial, msgNode);
            return msgNode.getFuture();
        }
        //process the msg
        return processAsync(msgNode);
    }

    private CompletableFuture<Void> processAsync(ProcessMsgNode node) {
        return CompletableFuture
            .runAsync(node::process)
            .thenAccept(v -> {
                node.sendAck();
                node.complete();
            })
            .thenAccept(v -> {
                lastId.set(node.getSerial());
                notContinuousMap.remove(node.getId());
            })
            .thenComposeAsync(v -> {
                Long nextId = nextId(node.getSerial());
                if (notContinuousMap.containsKey(nextId)) {
                    //there is a next msg waiting in the map
                    ProcessMsgNode nextNode = notContinuousMap.get(nextId);
                    return processAsync(nextNode);
                } else {
                    //that's the newest msg
                    return node.getFuture();
                }
            })
            .exceptionally(e -> {
                logger.error("[process received msg] has error", e);
                return null;
            });
    }


    private boolean isRepeat(Long msgId) {
        return msgId <= lastId.get();
    }

    private boolean isContinuous(Long msgId) {
        //如果是本次会话的第一条消息
        if (first.compareAndSet(true, false)) {
            return true;
        } else {
            //不是第一条消息，则按照公式算（如果同时有好几条第一条消息，除了真正的第一条，其他会返回false）
            return msgId - lastId.get() == 1;
        }
    }

    private Long nextId(Long id) {
        return id + 1;
    }

}
