package com.takiku.connector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZkConfig {
    @Value("${zk.root}")
    private String zkRoot;

    @Value("${zk.addr}")
    private String zkAddr;

    @Value("${zk.connect.timeout}")
    private int zkConnectTimeout;

    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }

    public int getZkConnectTimeout() {
        return zkConnectTimeout;
    }

    public void setZkConnectTimeout(int zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
    }
}
