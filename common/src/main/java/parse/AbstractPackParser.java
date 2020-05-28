package parse;

import com.google.protobuf.Message;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import exception.IMException;
import function.ImBiConsumer;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPackParser {

    private Logger logger = LoggerFactory.getLogger(AbstractPackParser.class);

    private Map<Class<? extends com.google.protobuf.GeneratedMessageV3>, ImBiConsumer<? extends Message, ChannelHandlerContext>> parserMap;
    protected AbstractPackParser() {
        this.parserMap = new HashMap<>();
        registerParsers();
    }
    /**
     * 注册pack处理方法
     */
    public abstract void registerParsers();


    protected <T extends com.google.protobuf.GeneratedMessageV3> void register(Class<T> clazz, ImBiConsumer<T, ChannelHandlerContext> consumer) {
        parserMap.put(clazz, consumer);
    }

    @SuppressWarnings("unchecked")
    public void parse(com.google.protobuf.GeneratedMessageV3 pack, ChannelHandlerContext ctx) {

        ImBiConsumer consumer = parserMap.get(pack.getClass());
        if (consumer == null) {
            logger.warn("[pack parser] unexpected msg: {}", new Throwable(pack.toString()));
        }
        doParse(consumer, pack.getClass(), pack, ctx);
    }

    private <T extends com.google.protobuf.GeneratedMessageV3 > void doParse(ImBiConsumer<T, ChannelHandlerContext> consumer, Class<T> clazz, com.google.protobuf.GeneratedMessageV3 msg, ChannelHandlerContext ctx) {
        T m = clazz.cast(msg);
        try {
            consumer.accept(m, ctx);
        } catch (Exception e) {
            throw new IMException("[msg parse] has error", e);
        }
    }


}
