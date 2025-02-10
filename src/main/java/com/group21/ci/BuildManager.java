package com.group21.ci;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * BuildManager is responsible for cloning the repository and executing the test
 * suite.
 * - Clones the repository from the provided GitHub owner and repo name.
 * - Runs `mvn test` to execute tests.
 * - Returns true if tests pass, otherwise false.
 */
public class BuildManager {

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

            // Clone the repository into a local "repo" directory
            Process clone = Runtime.getRuntime().exec("git clone " + repoUrl + " repo");
            clone.waitFor(); // Wait for cloning to complete

            // Run Maven tests inside the cloned repository
            Process mvnTest = Runtime.getRuntime().exec("cd repo && mvn test");
            printProcessOutput(mvnTest); // Print the test output

            // Return true if the test process exits with status 0 (success)
            return mvnTest.waitFor() == 0;
        } catch (Exception e) {
            // Print error details if build execution fails
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads and prints the output of a running process.
     * This method helps capture logs from the build and test execution.
     *
     * @param process The process whose output needs to be printed.
     * @throws Exception If an error occurs while reading the process output.
     */
    private static void printProcessOutput(Process process) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        // Read and print each line of process output
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Wait for process to complete before exiting
        process.waitFor();
    }
}