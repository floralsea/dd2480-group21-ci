package ci;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

/**
 * TestRunner is responsible for executing unit tests in the CI system.
 * - Runs `mvn test` to execute the project's test suite.
 * - Logs test results to a file.
 * - Determines whether the tests passed or failed.
 */
public class TestRunner {
    // Log file where test results will be recorded
    private static final String LOG_FILE = "test_results.log";

    /**
     * Executes the test suite using Maven (`mvn test`) and logs the results.
     * 
     * @return true if tests pass (contain "BUILD SUCCESS"), false otherwise.
     */
    @SuppressWarnings("deprecation")
    public static boolean runTests() {
        boolean success = false;

        try {
            System.out.println("Running tests...");

            // Execute Maven test command
            Process process = Runtime.getRuntime().exec("mvn test");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            FileWriter logWriter = new FileWriter(LOG_FILE, true); // Append to the log file

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print test output to console
                logWriter.write(LocalDateTime.now() + " - " + line + "\n"); // Write test output to log file

                // Check if the test execution was successful
                if (line.contains("BUILD SUCCESS")) {
                    success = true;
                }
            }

            process.waitFor(); // Wait for test execution to complete
            logWriter.close(); // Close the log file

            System.out.println("Tests completed. Success: " + success);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Log error details if test execution fails
        }

        return success; // Return whether the tests passed or failed
    }
}