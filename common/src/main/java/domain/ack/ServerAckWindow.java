package domain.ack;

import com.google.protobuf.Message;
import domain.ResponseCollector;
import exception.IMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerAckWindow {
    private static Logger logger = LoggerFactory.getLogger(ServerAckWindow.class);
    private static Map<Serializable, ServerAckWindow> windowsMap;
    private ConcurrentHashMap<String, ResponseCollector<PackProtobuf.Ack>> responseCollectorMap;

    private final Duration timeout;
    private final int maxSize;


    public ServerAckWindow(Serializable connectionId, int maxSize, Duration timeout) {
        this.responseCollectorMap = new ConcurrentHashMap<>();
        this.timeout = timeout;
        this.maxSize = maxSize;

        windowsMap.put(connectionId, this);
    }

    /**
     * multi thread do it
     *
     * @param msgId
     * @param sendMessage
     * @param sendFunction
     * @return
     */
    public static CompletableFuture<PackProtobuf.Ack> offer(Serializable connectionId, String msgId, Message sendMessage, Consumer<Message> sendFunction) {
        return windowsMap.get(connectionId).offer(msgId, sendMessage, sendFunction);
    }

    public CompletableFuture<PackProtobuf.Ack> offer(String msgId, Message sendMessage, Consumer<Message> sendFunction) {
        if (responseCollectorMap.containsKey(msgId)) {
            CompletableFuture<PackProtobuf.Ack> future = new CompletableFuture<>();
            future.completeExceptionally(new IMException("send repeat msg id: " + msgId));
            return future;
        }
        if (responseCollectorMap.size() >= maxSize) {
            CompletableFuture<PackProtobuf.Ack> future = new CompletableFuture<>();
            future.completeExceptionally(new IMException("server window is full"));
            return future;
        }
        logger.debug("get send, msg: {}", msgId);
        ResponseCollector<PackProtobuf.Ack> responseCollector = new ResponseCollector<>(sendMessage, sendFunction);
        responseCollector.send();
        responseCollectorMap.put(msgId, responseCollector);
        return responseCollector.getFuture();
    }

}
