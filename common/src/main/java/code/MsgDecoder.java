package code;


import io.netty.handler.codec.protobuf.ProtobufDecoder;


public class MsgDecoder extends ProtobufDecoder {

    public MsgDecoder(com.google.protobuf.MessageLite prototype) {
        super(prototype);
    }
}
