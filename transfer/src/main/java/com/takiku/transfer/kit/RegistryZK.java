package com.takiku.transfer.kit;


import com.takiku.transfer.config.SpringUtil;
import com.takiku.transfer.config.ZKConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class RegistryZK implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RegistryZK.class);


    private ZKit zKit;


    private ZKConfiguration zkConfiguration ;

    private String ip;
    private int transferPort;

    public RegistryZK(String ip, int transferPort) {
        this.ip = ip;
        this.transferPort = transferPort;
        zKit=SpringUtil.getBean(ZKit.class);
        zkConfiguration=SpringUtil.getBean(ZKConfiguration.class);
    }

    @Override
    public void run() {

        //创建父节点
        zKit.createRootNode();

        //是否要将自己注册到 ZK
        if (zkConfiguration.isZkSwitch()){
            String path = zkConfiguration.getZkRoot() + "/ip-" + ip + ":" + transferPort ;
            if (!zKit.isExits(path)){
                zKit.createNode(path);
                logger.info("注册 zookeeper 成功，msg=[{}]", path);
            }else {
                logger.info("msg=[{}] 已经存在 ", path);
            }

        }


    }
}