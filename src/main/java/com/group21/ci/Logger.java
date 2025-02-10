package com.group21.ci;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Logger class for recording CI server activities.
 * This class logs important events, errors, and build/test results to a file.
 */
public class Logger {
    // Name of the log file where CI logs will be stored
    private static final String LOG_FILE = "ci_logs.txt";

    /**
     * Logs a message to the log file with a timestamp.
     * 
     * @param message The message to log.
     */
    public static void log(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) { // Open file in append mode
            writer.write(LocalDateTime.now() + " - " + message + "\n"); // Write timestamped log entry
        } catch (IOException e) {
            e.printStackTrace(); // Print error details if file writing fails
        }
    }
}