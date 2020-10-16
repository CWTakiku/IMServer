package internal;

import protobuf.PackProtobuf;

public class InternalAck {
    public static PackProtobuf.Pack createAck(String msgId) {
        return PackProtobuf.Pack.newBuilder()
                .setPackType(PackProtobuf.Pack.PackType.ACK)
                .setAck(PackProtobuf.Ack.newBuilder()
                        .setAckType(Constants.MSG_ACK_TYPE).setAckMsgId(msgId).setResult(Constants.MSG_RESULT_OK).build())
                .build();


    }
}
