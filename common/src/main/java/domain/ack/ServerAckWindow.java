package domain.ack;

import com.google.protobuf.Message;
import domain.ResponseCollector;
import exception.IMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.PackProtobuf;

import java.io.Serializable;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ServerAckWindow {
    private static Logger logger = LoggerFactory.getLogger(ServerAckWindow.class);
    private static Map<Serializable, ServerAckWindow> windowsMap;
    private ConcurrentHashMap<Long, ResponseCollector<PackProtobuf.Ack>> responseCollectorMap;
    private static ExecutorService executorService;
    private final Duration timeout;
    private final int maxSize;

    /**
     * single thread do it
     */
    private static void checkTimeoutAndRetry() {
        while (true) {
            for (ServerAckWindow window : windowsMap.values()) {
                window.responseCollectorMap.entrySet().stream()
                        .filter(entry -> window.timeout(entry.getValue()))
                        .forEach(entry -> window.retry(entry.getKey(), entry.getValue()));
            }
        }
    }

    static {
        windowsMap = new ConcurrentHashMap<>();
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(ServerAckWindow::checkTimeoutAndRetry);
    }

    public ServerAckWindow(Serializable connectionId, int maxSize, Duration timeout) {
        this.responseCollectorMap = new ConcurrentHashMap<>();
        this.timeout = timeout;
        this.maxSize = maxSize;

        windowsMap.put(connectionId, this);
    }

    /**
     * multi thread do it
     *
     * @param serialId
     * @param sendMessage
     * @param sendFunction
     * @return
     */
    public static CompletableFuture<PackProtobuf.Ack> offer(Serializable connectionId, Long serialId, Message sendMessage, Consumer<Message> sendFunction) {
        return windowsMap.get(connectionId).offer(serialId, sendMessage, sendFunction);
    }

    public CompletableFuture<PackProtobuf.Ack> offer(Long serialId, Message sendMessage, Consumer<Message> sendFunction) {
        if (responseCollectorMap.containsKey(serialId)) {
            CompletableFuture<PackProtobuf.Ack> future = new CompletableFuture<>();
            future.completeExceptionally(new IMException("send repeat msg id: " + serialId));
            return future;
        }
        if (responseCollectorMap.size() >= maxSize) {
            CompletableFuture<PackProtobuf.Ack> future = new CompletableFuture<>();
            future.completeExceptionally(new IMException("server window is full"));
            return future;
        }
        logger.info("get send, msg: {}", serialId);
        ResponseCollector<PackProtobuf.Ack> responseCollector = new ResponseCollector<>(sendMessage, sendFunction);
        responseCollector.send();
        responseCollectorMap.put(serialId, responseCollector);
        return responseCollector.getFuture();
    }

    public void ack(Message message) {
        Long id = ((PackProtobuf.Ack) message).getSerial();
        logger.info("get ack, msg: {}", id);
        if (responseCollectorMap.containsKey(id)) {
            responseCollectorMap.get(id).getFuture().complete((PackProtobuf.Ack) message);
            responseCollectorMap.remove(id);
        }
    }

    private boolean timeout(ResponseCollector<?> collector) {
        return collector.getSendTime().get() != 0 && collector.timeElapse() > timeout.toNanos();
    }

    private void retry(Long id, ResponseCollector<?> collector) {
        logger.debug("retry msg: {}", id);
        //todo: if offline
        collector.send();
    }
}
