package com.takiku.connector.service;


import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.takiku.connector.config.ConnectorRestServiceFactory;
import com.takiku.connector.config.RestConfig;
import com.takiku.connector.config.SpringUtil;
import com.takiku.connector.service.rest.ConnectorRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parse.ParseService;
import po.BaseResponse;
import po.Offline;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Date: 2019-05-28
 * Time: 00:24
 * <p>
 * 离线服务
 */
@Service
public class OfflineService {


    private ConnectorRestService connectorRestService;
    private ParseService parseService;


    public OfflineService() {
        parseService = new ParseService();
        connectorRestService = SpringUtil.getBean(ConnectorRestService.class);
    }

    /**
     * 取出离线消息
     *
     * @param token
     * @return
     */
    public List<Message> pollOfflineMsg(String userId,String token) {
        List<Offline> msgs = connectorRestService.offlines(token);
        if (msgs!=null){
            return msgs.stream()
                    .map(o -> {
                        try {
                            return parseService.getMsgByCode(o.getMsgCode(), o.getContent());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
        }else {
            return null;
        }

    }
}
