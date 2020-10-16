package com.takiku.restweb.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.takiku.restweb.dao.Hib;
import com.takiku.restweb.db.OfflineMsg;
import com.takiku.restweb.service.OfflineService;
import com.takiku.restweb.task.OfflineListen;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import po.Offline;
import protobuf.PackProtobuf;
import util.BytesUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfflineServiceImpl implements OfflineService {


    @Autowired
    Hib hib;
    private Logger logger = LoggerFactory.getLogger(OfflineServiceImpl.class);
    @Override
    public void saveChat(PackProtobuf.Msg msg) {

        OfflineMsg offlineMsg = new OfflineMsg();
        offlineMsg.setId(msg.getHead().getMsgId());
        offlineMsg.setContent(new String(msg.toByteArray()));
        logger.info(" save chat byte "+BytesUtil.bytes2hexStr(msg.toByteArray()));
        offlineMsg.setToUserId(msg.getHead().getToId());
        offlineMsg.setFromUserId(msg.getHead().getFromId());
        offlineMsg.setHasRead(false);
        offlineMsg.setType(PackProtobuf.Pack.PackType.MSG_VALUE);

        hib.query(session -> {
            session.save(offlineMsg);
            // 写入到数据库
            session.flush();

            // 紧接着从数据库中查询出来
          //  session.refresh(offlineMsg);
            return offlineMsg;
        });
    }

    @Override
    public void saveReply(PackProtobuf.Reply msg) {

    }

    @Override
    public List<Offline> pollOfflineMsg(String userId) throws JsonProcessingException {
        List<OfflineMsg> offlineMsgs =   hib.query(new Hib.Query<List<OfflineMsg>>() {
            @Override
            public List<OfflineMsg> query(Session session) {
                List<OfflineMsg> offlineMsgs =session.createQuery("from OfflineMsg where toUserId=:toUserId and hasRead=:hasRead")
                        .setParameter("toUserId",userId)
                        .setParameter("hasRead",false)
                        .list();
                offlineMsgs.forEach(offlineMsg -> {
                    offlineMsg.setHasRead(true);
                    session.save(offlineMsg);
                }
                );
                session.flush();
                return offlineMsgs;
            }
        });
        List<Offline> offlineList = new ArrayList<>();
        if (offlineMsgs!=null){
            for (OfflineMsg offlineMsg :offlineMsgs){
                Offline offline = new Offline();
                offline.setContent(offlineMsg.getContent());
                offline.setMsgCode(offlineMsg.getType());
                offline.setToUserId(offlineMsg.getToUserId());
                offline.setMsgId(offlineMsg.getId());
                offlineList.add(offline);
            }
        }
       // System.out.println("pollOfflineMsg size "+offlineList.size());
        return offlineList;
    }
}
