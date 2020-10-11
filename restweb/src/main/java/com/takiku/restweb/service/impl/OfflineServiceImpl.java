package com.takiku.restweb.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.takiku.restweb.service.OfflineService;
import org.springframework.stereotype.Service;
import po.Offline;
import protobuf.PackProtobuf;

import java.util.List;

@Service
public class OfflineServiceImpl implements OfflineService {
    @Override
    public void saveChat(PackProtobuf.Msg msg) {

    }

    @Override
    public void saveReply(PackProtobuf.Reply msg) {

    }

    @Override
    public List<Offline> pollOfflineMsg(String userId) throws JsonProcessingException {
        return null;
    }
}
