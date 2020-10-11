package parse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import exception.IMException;
import protobuf.PackProtobuf;

import java.util.HashMap;
import java.util.Map;


public class ParseService {
    private Map<Integer, Parse> parseFunctionMap;

    public ParseService() {
        parseFunctionMap = new HashMap<>();
        parseFunctionMap.put(PackProtobuf.Pack.PackType.MSG.getNumber(), PackProtobuf.Msg::parseFrom);
        parseFunctionMap.put(PackProtobuf.Pack.PackType.REPLY.getNumber(), PackProtobuf.Reply::parseFrom);
    }

    public Message getMsgByCode(int code, byte[] bytes) throws InvalidProtocolBufferException {

        Parse parseFunction = parseFunctionMap.get(code);
        if (parseFunction == null) {
            throw new IMException("[msg parse], no proper parse function, msgType: " + code);
        }
        return parseFunction.process(bytes);
    }

    @FunctionalInterface
    public interface Parse {
        /**
         * parse msg
         *
         * @param
         * @return
         * @throws InvalidProtocolBufferException
         */
        Message process(byte[] bytes) throws InvalidProtocolBufferException;
    }
}
