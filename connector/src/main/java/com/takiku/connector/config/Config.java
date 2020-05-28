package com.takiku.connector.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.takiku.connector.connectorserver.ConnectorServer;
import com.takiku.connector.service.rest.ConnectorRestService;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${netty.server.port}")
    private String port;

    @Autowired
    RestConfig restConfig;
    @Autowired
    ZkConfig zkConfig;

    @Bean
    public LoadingCache<String, String> cache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return null;
                    }
                });
    }

    @Bean
    public ConnectorServer connectorServer() throws Exception {
        ConnectorServer connectorServer=new ConnectorServer(port);
        connectorServer.startServer();
       return connectorServer;
    }
    @Bean
    public ConnectorRestService connectorRestService(){
        ConnectorRestService connectorRestService=new ConnectorRestService(restConfig.getRestUrl());
        return connectorRestService;
    }

    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(zkConfig.getZkAddr(), zkConfig.getZkConnectTimeout());
    }
}
