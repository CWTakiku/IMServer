package com.takiku.connector.kit;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZKit {
    private static Logger logger = LoggerFactory.getLogger(ZKit.class);

    @Autowired
    private ZkClient zkClient;
    @Autowired
    private TransferCache transferCache;


    /**
     * 监听事件
     *
     * @param path
     */
    public void subscribeEvent(String path) {
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                logger.info("清除/更新本地缓存 parentPath=【{}】,currentChilds=【{}】", parentPath,currentChilds.toString());

                //更新所有缓存/先删除 再新增
                transferCache.updateCache(currentChilds) ;
            }
        });

    }


    /**
     * 获取所有注册节点
     * @return
     */
    public List<String> getAllNode(){
        List<String> children = zkClient.getChildren("/transfer");
       // logger.info("查询所有节点成功=【{}】", JSON.toJSONString(children));
        return children;
    }

}
