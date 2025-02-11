package com.group21.ci.dao;

import com.group21.ci.entity.TestResultEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;


/**
 * Data Access Object for table TestResult
 */
public class TestResultDAO {
    private final SessionFactory sessionFactory;

    public TestResultDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public TestResultDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Insert a record into database
     * @param testResult
     */
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


    /**
     * Look up a record by its commitSHA
     * @param commitSha
     * @return a TestResultEntity
     */
    public TestResultEntity getTestResultByCommitSha(String commitSha) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM TestResultEntity WHERE commitSha = :commitSha", TestResultEntity.class)
                    .setParameter("commitSha", commitSha)
                    .uniqueResult();
        }
    }


    /**
     * Get all records
     * @return a list of TestResultEntity
     */
    public List<TestResultEntity> getAllTestResults() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM TestResultEntity", TestResultEntity.class).list();
        }
    }

    /**
     * Delete a record by its CommitSHA
     * @param commitSha
     */
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