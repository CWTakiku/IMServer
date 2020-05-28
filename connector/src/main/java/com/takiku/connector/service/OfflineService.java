package com.takiku.connector.service;


import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.takiku.connector.config.ConnectorRestServiceFactory;
import com.takiku.connector.config.RestConfig;
import com.takiku.connector.service.rest.ConnectorRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import po.BaseResponse;
import po.Offline;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Date: 2019-05-28
 * Time: 00:24
 *
 * @author yrw
 */
@Service
public class OfflineService {

    @Autowired
    private ConnectorRestService connectorRestService;




    public OfflineService() {

    //    this.parseService = parseService;
    }

    public List<Message> pollOfflineMsg(String userId) {
       BaseResponse<List<Offline> > msgs = connectorRestService.offlines(userId);
        return null;
//        return msgs.stream()
//            .map(o -> {
//                try {
//                  //  return parseService.getMsgByCode(o.getMsgCode(), o.getContent());
//                } catch (InvalidProtocolBufferException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }).filter(Objects::nonNull).collect(Collectors.toList());
//    }
    }
}
