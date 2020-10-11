package com.takiku.transfer;

import com.takiku.transfer.config.ZKConfiguration;
import com.takiku.transfer.kit.RegistryZK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

@SpringBootApplication
public class TransferApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TransferApplication.class, args);
    }

    @Autowired
    private ZKConfiguration zkConfiguration;

    @Override
    public void run(String... args) throws Exception {
        //获得本机IP
        String addr = InetAddress.getLocalHost().getHostAddress();
        Thread thread = new Thread(new RegistryZK(addr, zkConfiguration.getTransferPort()));
        thread.setName("registry-zk");
        thread.start();
    }
}
