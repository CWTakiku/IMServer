package com.takiku.transfer.domain;


import com.google.inject.Singleton;
import com.takiku.userstatus.service.UserStatusService;
import conn.ConnectorConn;
import conn.MemoryConnContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 存储transfer和connector的连接
 * 以及用户和connector的关系
 * Date: 2019-04-12
 * Time: 18:22
 *
 * @author yrw
 */
@Component
@Singleton
public class ConnectorConnContext extends MemoryConnContext<ConnectorConn> {

   @Autowired
    private UserStatusService userStatusService;


    public ConnectorConnContext()
    {
//        userStatusService = new RedisUserStatusServiceImpl();
   }

    public ConnectorConn getConnByUserId(String userId) {
        String connectorId = userStatusService.getConnectorId(userId);
        if (connectorId != null) {
            ConnectorConn conn = getConn(connectorId);
            if (conn != null) {
                return conn;
            } else {
                //connectorId已过时，而用户还没再次上线
                userStatusService.offline(userId);
            }
        }
        return null;
    }
}
