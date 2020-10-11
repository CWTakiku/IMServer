package com.takiku.connector.service.impl;

import com.takiku.connector.service.UserStatusService;
import com.takiku.connector.config.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class RedisUserStatusServiceImpl implements UserStatusService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserStatusServiceImpl.class);

    private static final String USER_CONN_STATUS_KEY = "IM:USER_CONN_STATUS:USERID:";


    public RedisUserStatusServiceImpl() {

    }

    public String online(String userId, String connectorId) {
        logger.debug("[user status] user online: userId: {}, connectorId: {}", userId, connectorId);
        String oldConnectorId = (String) RedisUtil.redisUtil.hget(USER_CONN_STATUS_KEY, String.valueOf(userId));
        RedisUtil.redisUtil.hset(USER_CONN_STATUS_KEY, String.valueOf(userId), connectorId);
        return oldConnectorId;
    }

    public void offline(String userId) {
        RedisUtil.redisUtil.hdel(USER_CONN_STATUS_KEY, String.valueOf(userId));
    }

    public String getConnectorId(String userId) {

        return (String) RedisUtil.redisUtil.hget(USER_CONN_STATUS_KEY, String.valueOf(userId));
    }
}
