package com.group21.ci;

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

    /**
     * Executes the build process for a given repository.
     * - Constructs the repository URL dynamically.
     * - Clones the repository into a local directory.
     * - Runs `mvn test` to execute tests.
     * - Returns true if tests pass successfully, otherwise false.
     *
     * @param repoOwner The owner of the repository.
     * @param repoName  The name of the repository.
     * @return true if tests pass successfully, false otherwise.
     */
    @SuppressWarnings("deprecation")
    public static boolean runBuild(String repoOwner, String repoName) {
        try {
            // Construct the repository URL dynamically
            String repoUrl = "https://github.com/" + repoOwner + "/" + repoName + ".git";

            System.out.println("Cloning repository...");

            // Clone the repository into a local "repo" directory
            Process clone = Runtime.getRuntime().exec("git clone " + repoUrl + " repo");
            clone.waitFor(); // Wait for cloning to complete

            System.out.println("Running tests in the cloned repository...");

            ProcessBuilder mvnTestBuilder = new ProcessBuilder("mvn", "test");
            mvnTestBuilder.directory(new File("repo"));
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

            mvnTest.waitFor(); // Wait for the test execution to complete
            logWriter.close(); // Close the log file


            // Return true if tests pass and build succeeds
            return success && mvnTest.waitFor() == 0;
        } catch (Exception e) {
            // Print error details if build execution fails
            e.printStackTrace();
            return false;
        }
    }
}