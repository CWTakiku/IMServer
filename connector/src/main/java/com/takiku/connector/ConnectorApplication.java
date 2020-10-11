package com.takiku.connector;

import com.takiku.connector.kit.ServerListListener;
import com.takiku.connector.kit.TransferCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

//自动加载配置信息
@EnableAutoConfiguration(exclude = {JpaRepositoriesAutoConfiguration.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan({("com.takiku.connector.config"), ("com.takiku.connector.service"),
        ("com.takiku.connector.domain"), ("com.takiku.connector.handler"),
        ("com.takiku.connector.kit")})
public class ConnectorApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ConnectorApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(ConnectorApplication.class, args);
        log.info("启动 onnectorApplication 成功");
    }


    @Override
    public void run(String... args) throws Exception {
        //监听服务
        Thread thread = new Thread(new ServerListListener());
        thread.setName("zk-listener");
        thread.start();
    }
}
