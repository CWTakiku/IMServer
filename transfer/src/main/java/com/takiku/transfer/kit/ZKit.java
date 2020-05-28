package com.takiku.transfer.kit;


import com.takiku.transfer.config.ZKConfiguration;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class ZKit {

    private static Logger logger = LoggerFactory.getLogger(ZKit.class);


    @Autowired
    private ZkClient zkClient;

    @Autowired
    private ZKConfiguration zkConfiguration ;

    /**
     * 创建父级节点
     */
    public void createRootNode(){
        boolean exists = zkClient.exists(zkConfiguration.getZkRoot());
        if (exists){
            return;
        }

        //创建 root
        zkClient.createPersistent(zkConfiguration.getZkRoot()) ;
    }

    /**
     * 写入指定节点 临时目录
     *
     * @param path
     */
    public void createNode(String path) {
        zkClient.createEphemeral(path);
    }
    public boolean isExits(String path){
      return   zkClient.exists(path);
    }

}