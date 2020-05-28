package com.takiku.transfer.config;

import com.takiku.transfer.mqproducer.TransferMqProducer;
import com.takiku.transfer.transferserver.TransferServer;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class TransferConfig {

    @Value("${netty.transfer.port}")
    private Integer port;

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;

    @Value("${rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${log.path}")
    private String logPath;

    @Value("${log.level}")
    private String logLevel;

    @Autowired
    ZKConfiguration zkConfiguration;


    @Bean
    public TransferMqProducer TransferMqProducer() throws IOException, TimeoutException {
         return new TransferMqProducer(rabbitmqHost,rabbitmqPort,rabbitmqUsername,rabbitmqPassword);
    }
    @Bean
    public TransferServer transferServer(){
        TransferServer transferServer=new TransferServer();
        transferServer.startTransferServer(port);
        return transferServer;
    }

    @Bean
    public ZkClient buildZKClient(){
        return new ZkClient(zkConfiguration.getZkAddr(), zkConfiguration.getZkConnectTimeout());
    }

}
