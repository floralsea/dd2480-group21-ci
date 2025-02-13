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
            // Construct the repository URL dynamically
            String repoUrl = "https://github.com/" + repoOwner + "/" + repoName + ".git";


            System.out.println("Cloning repository...");


            // Clone the repository into a local "repo" directory
            Process clone = Runtime.getRuntime().exec("git clone --branch " + branchName + " " + repoUrl + " repo");
            clone.waitFor(); // Wait for cloning to complete


            System.out.println("Running tests in the cloned repository...");


            // Run Maven test
            ProcessBuilder mvnTestBuilder = new ProcessBuilder("mvn", "test");
            File repoDirectory = new File("repo");
            mvnTestBuilder.directory(repoDirectory);
            Process mvnTest = mvnTestBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(mvnTest.getInputStream()));
            FileWriter logWriter = new FileWriter(LOG_FILE, true); // Append to the log file
            String line;
            boolean success = false;


            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print test output to console
                logWriter.write(LocalDateTime.now() + " - " + line + "\n"); // Write test output to log file


                // Check if the test execution was successful
                if (line.contains("BUILD SUCCESS")) {
                    success = true;
                }
            }


            // Capture test logs
            StringBuilder logOutput = new StringBuilder();
            try (BufferedReader logReader = new BufferedReader(
                    new InputStreamReader(mvnTest.getInputStream()))) {
                String logLine;
                while ((logLine = logReader.readLine()) != null) {
                    logOutput.append(logLine).append("\n");
                }
            }
            // mvnTest.waitFor(); // Wait for the test execution to complete
            boolean testSuccess = (mvnTest.waitFor() == 0);
            // Store test result in database
            TestResultEntity testResult = new TestResultEntity(
                    commitSha,
                    testSuccess ? TestStatus.SUCCESS : TestStatus.FAILED,
                    logOutput.toString(),
                    LocalDateTime.now()
            );
            testResultDAO.saveTestResult(testResult);
            logWriter.close(); // Close the log file


            // Clean up: Delete the cloned repository directory
            deleteDirectory(repoDirectory);

            // Return true if tests pass and build succeeds
//            return success && mvnTest.waitFor() == 0;
            return testSuccess && success;
        } catch (Exception e) {


            // Print error details if build execution fails
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
