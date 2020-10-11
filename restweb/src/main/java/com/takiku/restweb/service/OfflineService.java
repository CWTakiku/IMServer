package com.takiku.restweb.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import po.Offline;
import protobuf.PackProtobuf;

import java.util.List;

/**
 * Date: 2019-05-05
 * Time: 09:48
 *
 * @author yrw
 */
public interface OfflineService {

    /**
     * save offline chat msg
     *
     * @param msg
     * @return
     */
    void saveChat(PackProtobuf.Msg msg);

    /**
     * save offline ack msg
     *
     * @param msg
     * @return
     */
    void saveReply(PackProtobuf.Reply msg);

    /**
     * get a user's all offline msgs
     *
     * @param userId
     * @return
     * @throws JsonProcessingException
     */
    List<Offline> pollOfflineMsg(String userId) throws JsonProcessingException;
}