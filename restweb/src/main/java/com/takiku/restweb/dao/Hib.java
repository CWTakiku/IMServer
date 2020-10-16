package com.takiku.restweb.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by qiujuer
 * on 2017/2/17.
 */

@Transactional
@Service
public class Hib {
//    @Bean
//    public SessionFactory sessionFactory(@Qualifier("entityManagerFactory") EntityManagerFactory emf){
//        return emf.unwrap(SessionFactory.class);
//    }

    // 全局SessionFactory
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

////
//    static {
//        // 静态初始化sessionFactory
//        init();
//    }



//    @Bean
//    public static SessionFactory getSessionFactory() {
//        if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
//            throw new NullPointerException("factory is not a hibernate factory");
//            LocalSessionFactoryBean
//        }
//        return entityManagerFactory.unwrap(SessionFactory.class);
//    }

//    private static void init() {
//        // 从hibernate.cfg.xml文件初始化
//        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//                .configure() // configures settings from hibernate.cfg.xml
//                .build();
//        try {
//            // build 一个sessionFactory
//            sessionFactory = new MetadataSources(registry)
//                    .buildMetadata()
//                    .buildSessionFactory();
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 错误则打印输出，并销毁
//            StandardServiceRegistryBuilder.destroy(registry);
//        }
//    }

    /**
     * 获取全局的SessionFactory
     *
     * @return SessionFactory
     */
    public SessionFactory sessionFactory() {
        return sessionFactory;
    }

    /**
     * 从SessionFactory中得到一个Session会话
     *
     * @return Session
     */
    public Session session() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 关闭sessionFactory
     */
    public  void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }


    // 用户的实际的操作的一个接口
    public interface QueryOnly {
        void query(Session session);
    }

    // 简化Session事物操的一个工具方法
    public     void queryOnly(QueryOnly query) {
        // 重开一个Session
        Session session = sessionFactory.openSession();
        // 开启事物
        final Transaction transaction = session.beginTransaction();

        try {
            // 调用传递进来的接口，
            // 并调用接口的方法把Session传递进去
            query.query(session);
            // 提交
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚
            try {
                transaction.rollback();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            }
        } finally {
            // 无论成功失败，都需要关闭Session
            session.close();
        }

    }


    // 用户的实际的操作的一个接口
    // 具有返回值T
    public interface Query<T> {
        T query(Session session);
    }

    // 简化Session操作的工具方法，
    // 具有一个返回值
    public     <T> T query(Query<T> query) {
        // 重开一个Session
        Session session =  sessionFactory.openSession();
        // 开启事物
        final Transaction transaction = session.beginTransaction();

        T t = null;
        try {
            // 调用传递进来的接口，
            // 并调用接口的方法把Session传递进去
            t = query.query(session);
            // 提交
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚
            try {
                transaction.rollback();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            }
        } finally {
            // 无论成功失败，都需要关闭Session
            session.close();
        }

        return t;
//        DaoService daoService=new DaoService();
//      return   daoService.query(query);
    }


}
