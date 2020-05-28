package com.takiku.connector.kit;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.takiku.connector.connectorserver.ConnectorTransfer;
import com.takiku.connector.kit.ZKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
@Singleton
public class TransferCache {



    private Map<String, String> cache=new ConcurrentHashMap<>();

    @Autowired
    private ZKit zkUtil;

    private AtomicLong index = new AtomicLong();


    public void addCache(String key) {
        cache.put(key, key);
    }


    /**
     * 更新所有缓存/先删除 再新增
     *
     * @param currentChilds
     */
    public void updateCache(List<String> currentChilds) {

        for (String currentChild : currentChilds) {
            String key = currentChild.split("-")[1];
            System.out.println("key --- "+ key);

            if (cache.containsKey(key)){

            }else {
                toConnectTransfer(key);
            }
            addCache(key);
        }
    }

    private void toConnectTransfer(String key) {
        ConnectorTransfer connectorTransfer=new ConnectorTransfer();
        connectorTransfer.start(key);
    }


    /**
     * 获取所有的服务列表
     *
     * @return
     */
    public List<String> getAll() {

        List<String> list = new ArrayList<>();

        if (cache.size() == 0) {
            List<String> allNode = zkUtil.getAllNode();
            for (String node : allNode) {
                String key = node.split("-")[1];
                addCache(key);
            }
        }
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            list.add(entry.getKey());
        }
        return list;

    }

    /**
     * 选取服务器
     *
     * @return
     */
    public String selectServer() {
        List<String> all = getAll();
        if (all.size() == 0) {
            throw new RuntimeException("transfer 服务器可用服务列表为空");
        }
        Long position = index.incrementAndGet() % all.size();
        if (position < 0) {
            position = 0L;
        }

        return all.get(position.intValue());
    }
}
