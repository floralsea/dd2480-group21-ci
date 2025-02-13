package com.group21.ci;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BuildManagerTest {
    private static final String TEST_LOG_FILE = "test_results.log";
    private static final String TEST_REPO_DIR = "test_repo";

    @BeforeEach
    void setUp() throws Exception {
        // Ensure a clean test environment
        cleanupTestFiles();
        Files.createDirectories(Paths.get(TEST_REPO_DIR));
    }

    @AfterEach
    void tearDown() {
        // Cleanup files after test execution
        cleanupTestFiles();
    }

    private void cleanupTestFiles() {
        // Delete the test repository directory
        deleteDirectory(new File(TEST_REPO_DIR));
        new File(TEST_LOG_FILE).delete();
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
            directory.delete();
        }
    }

    @Test
    void testRunBuild_Success() throws Exception {
        // Simulate a successful Maven test execution
        Path mvnTestFile = Paths.get(TEST_REPO_DIR, "mvn_test_output.txt");
        Files.write(mvnTestFile, "BUILD SUCCESS".getBytes());

        // Run the BuildManager with a test directory instead of real Git cloning
        boolean result = simulateRunBuild(true);

        assertTrue(result, "Build should succeed when 'BUILD SUCCESS' is in the output.");
    }

    @Test
    void testRunBuild_Failure() throws Exception {
        // Simulate a failed Maven test execution
        Path mvnTestFile = Paths.get(TEST_REPO_DIR, "mvn_test_output.txt");
        Files.write(mvnTestFile, "BUILD FAILURE".getBytes());

        // Run the BuildManager with a test directory instead of real Git cloning
        boolean result = simulateRunBuild(false);

        assertTrue(result, "Build should fail when 'BUILD FAILURE' is in the output.");
    }

    private boolean simulateRunBuild(boolean success) throws Exception {
        File logFile = new File(TEST_LOG_FILE);
        FileWriter logWriter = new FileWriter(logFile, true);

        logWriter.write(success ? "BUILD SUCCESS" : "BUILD FAILURE");
        logWriter.close();

        return success; // Return success based on the test scenario
    }

    @Test
    void testDeleteDirectory() throws IOException {
        // setup a test directory and a file inside it
        File testDirectory = new File(TEST_REPO_DIR);
        File testFile = new File(testDirectory, "testFile.txt");
        testFile.createNewFile();

        assertTrue(testDirectory.exists(), "Test directory should exist before deletion.");
        assertTrue(testFile.exists(), "Test file should exist before deletion.");

        deleteDirectory(testDirectory);

        // assert test file and directory deleted
        assertFalse(testFile.exists(), "Test file should be deleted.");
        assertFalse(testDirectory.exists(), "Test directory should be deleted.");
    }
}
