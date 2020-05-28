package internal;

import protobuf.PackProtobuf;

public class InternalAck {
    public static PackProtobuf.Pack createAck(String msgId,int ackType,int result){
        return PackProtobuf.Pack.newBuilder()
                .setPackType(PackProtobuf.Pack.PackType.ACK)
                .setAck(PackProtobuf.Ack.newBuilder()
                        .setAckType(ackType).setAckMsgId(msgId).setResult(result).build())
                .build();
    }
}
