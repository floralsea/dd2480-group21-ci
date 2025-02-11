package com.group21.ci.dao;

import com.group21.ci.entity.TestResultEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class TestResultDAO {
    private final SessionFactory sessionFactory;

    public TestResultDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public TestResultDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    public void saveTestResult(TestResultEntity testResult) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(testResult);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public TestResultEntity getTestResultByCommitSha(String commitSha) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM TestResultEntity WHERE commitSha = :commitSha", TestResultEntity.class)
                    .setParameter("commitSha", commitSha)
                    .uniqueResult();
        }
    }

    public List<TestResultEntity> getAllTestResults() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM TestResultEntity", TestResultEntity.class).list();
        }
    }

    public void deleteTestResult(String commitSha) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            TestResultEntity testResult = getTestResultByCommitSha(commitSha);
            if (testResult != null) {
                session.delete(testResult);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}