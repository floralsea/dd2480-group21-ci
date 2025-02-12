package com.group21.ci.dao;

import com.group21.ci.entity.TestResultEntity;
import com.group21.ci.entity.TestStatus;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestResultDAOTest {
    private static TestResultDAO testResultDAO;

    @BeforeAll
    static void setup() {
        String dbType = System.getProperty("dbType", "h2"); // Default to H2
        if ("mysql".equalsIgnoreCase(dbType)) {
            HibernateTestUtil.init("hibernate-mysql-test.cfg.xml");
        } else {
            HibernateTestUtil.init("hibernate-test.cfg.xml");
        }
        testResultDAO = new TestResultDAO(HibernateTestUtil.getSessionFactory());
    }

    @BeforeEach
    void cleanupDatabase() {
        try (Session session = HibernateTestUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM TestResultEntity").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void testSaveAndRetrieveTestResult() {
        TestResultEntity testResult = new TestResultEntity(
                "commit123",
                TestStatus.SUCCESS,
                "Tests passed successfully.",
                LocalDateTime.now()
        );

        testResultDAO.saveTestResult(testResult);

        TestResultEntity fetchedResult = testResultDAO.getTestResultByCommitSha("commit123");

        assertNotNull(fetchedResult);
        assertEquals(TestStatus.SUCCESS, fetchedResult.getStatus());
        assertEquals("Tests passed successfully.", fetchedResult.getTestLog());
    }

    @Test
    void testGetAllTestResults() {
        TestResultEntity test1 = new TestResultEntity(
                "commitA",
                TestStatus.SUCCESS,
                "Build successful",
                LocalDateTime.now()
        );
        TestResultEntity test2 = new TestResultEntity(
                "commitB",
                TestStatus.FAILED,
                "Build failed due to compilation error",
                LocalDateTime.now()
        );

        testResultDAO.saveTestResult(test1);
        testResultDAO.saveTestResult(test2);

        List<TestResultEntity> allResults = testResultDAO.getAllTestResults();
        assertEquals(2, allResults.size());
    }

    @Test
    void testDeleteTestResult() {
        TestResultEntity testResult = new TestResultEntity(
                "commitDelete",
                TestStatus.SUCCESS,
                "Tests passed successfully.",
                LocalDateTime.now()
        );

        testResultDAO.saveTestResult(testResult);
        assertNotNull(testResultDAO.getTestResultByCommitSha("commitDelete"));

        testResultDAO.deleteTestResult("commitDelete");
        assertNull(testResultDAO.getTestResultByCommitSha("commitDelete"));
    }
}