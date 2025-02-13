package com.group21.ci;


import com.group21.ci.dao.TestResultDAO;
import com.group21.ci.entity.TestResultEntity;
import com.group21.ci.entity.TestStatus;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDateTime;


/**
 * BuildManager is responsible for cloning the repository and executing the test
 * suite.
 * - Clones the repository from the provided GitHub owner and repo name.
 * - Runs `mvn test` to execute tests.
 * - Returns true if tests pass, otherwise false.
 */
public class BuildManager {
    private static final String LOG_FILE = "test_results.log";
    private static final TestResultDAO testResultDAO = new TestResultDAO();
    /**
     * Executes the build process for a given repository.
     * - Constructs the repository URL dynamically.
     * - Clones the repository into a local directory.
     * - Runs `mvn test` to execute tests.
     * - Returns true if tests pass successfully, otherwise false.
     * - Clears the cloned repository directory after execution.
     *
     * @param repoOwner The owner of the repository.
     * @param repoName  The name of the repository.
     * @param branchName    The branch to be tested of the repository.
     * @return true if tests pass successfully, false otherwise.
     */
    @SuppressWarnings("deprecation")
    public static boolean runBuild(String repoOwner, String repoName, String branchName, String commitSha) {
        try {
            String repoUrl = "https://github.com/" + repoOwner + "/" + repoName + ".git";
            System.out.println("Cloning repository...");

            // Clone the repository
            Process clone = Runtime.getRuntime().exec("git clone --branch " + branchName + " " + repoUrl + " repo");
            clone.waitFor();

            // Ensure the latest code is pulled
            ProcessBuilder pullProcess = new ProcessBuilder("git", "pull");
            pullProcess.directory(new File("repo"));
            pullProcess.start().waitFor();

            System.out.println("Running tests in the cloned repository...");

            ProcessBuilder mvnTestBuilder = new ProcessBuilder("mvn", "clean", "test");
            File repoDirectory = new File("repo");
            mvnTestBuilder.directory(repoDirectory);
            Process mvnTest = mvnTestBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(mvnTest.getInputStream()));
            FileWriter logWriter = new FileWriter(LOG_FILE, true);
            String line;
            boolean hasFailures = false;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                logWriter.write(LocalDateTime.now() + " - " + line + "\n");

                // Check for failures in test output
                if (line.contains("Failures:") && !line.contains("Failures: 0")) {
                    hasFailures = true;
                }
            }

            int exitCode = mvnTest.waitFor();
            boolean testSuccess = (exitCode == 0);

            // Store test result in database
            TestResultEntity testResult = new TestResultEntity(
                    commitSha,
                    testSuccess && !hasFailures ? TestStatus.SUCCESS : TestStatus.FAILED,
                    "", // Log output not needed in DB
                    LocalDateTime.now()
            );
            testResultDAO.saveTestResult(testResult);

            System.out.println("Test result stored: " + (testResult.getStatus() == TestStatus.SUCCESS ? "SUCCESS" : "FAILED"));

            logWriter.close();
//            deleteDirectory(repoDirectory);

            return testSuccess && !hasFailures;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Helper method to deletes a directory its contents.
     *
     * @param directory The directory to be deleted.
     */
    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}


