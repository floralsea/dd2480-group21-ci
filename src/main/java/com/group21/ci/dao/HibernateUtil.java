package com.group21.ci.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * A Utility class providing Hibernate Instance for production
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Load default hibernate.cfg.xml and build sessionFactory
     * Connect to Mysql Server
     * @return SessionFactory
     */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}