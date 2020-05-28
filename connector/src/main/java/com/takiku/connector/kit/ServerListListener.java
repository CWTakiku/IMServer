package com.takiku.connector.kit;


import com.takiku.connector.config.SpringUtil;
import com.takiku.connector.config.ZkConfig;
import org.apache.zookeeper.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class ServerListListener implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(ServerListListener.class);


    private ZKit zkUtil;



    private ZkConfig zkConfig;

    public ServerListListener() {
      zkUtil= SpringUtil.getBean(ZKit.class);
      zkConfig=SpringUtil.getBean(ZkConfig.class);
    }

    @Override
    public void run() {
        zkUtil.subscribeEvent(zkConfig.getZkRoot());
    }
}
