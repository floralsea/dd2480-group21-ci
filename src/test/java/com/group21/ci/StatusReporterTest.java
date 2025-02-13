package com.group21.ci;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusReporterTest {

    @Test
    void testSendStatus_successInput() {
        // Arrange: Set up the output stream to capture the printed content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act: Call the method we want to test
        StatusReporter.sendStatus("user", "repo", "commit123", true);

        // Assert: Check if the correct payload is printed
        String expectedPayload = "{ \"state\": \"success\", \"description\": \"CI Build passed\", \"context\": \"ci-server\"}";
        assertTrue(outContent.toString().contains(expectedPayload));

        // Reset System.out
        System.setOut(System.out);
    }

    @Test
    void testSendStatus_FailureInput() {
        // Arrange: Set up the output stream to capture the printed content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act: Call the method we want to test with failure status
        StatusReporter.sendStatus("user", "repo", "commit123", false);

        // Assert: Check if the correct payload for failure is printed
        String expectedPayload = "{ \"state\": \"failure\", \"description\": \"CI Build failed\", \"context\": \"ci-server\"}";
        assertTrue(outContent.toString().contains(expectedPayload));

        // Reset System.out
        System.setOut(System.out);
    }

}
