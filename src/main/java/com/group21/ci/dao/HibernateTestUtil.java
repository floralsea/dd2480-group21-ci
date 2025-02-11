package com.group21.ci.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * A Utility class providing Hibernate Instance for testing
 */
public class HibernateTestUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Load the testing configuration file and build SessionFactory
     * @return SessionFactory
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Load the test configuration file instead of the default one
            return new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}