package com.group21.ci;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildManagerTest {

    @BeforeEach
    void setUp() {
        // Clean up test logs before each test
        // Ensure a clean state before each test by deleting the test results log file if it exists
        File logFile = new File("test_results.log");
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    @Test
    void testRunBuildSuccess() throws Exception {
        // Test case: Verify that BuildManager.runBuild() returns true when the build (mvn test) is successful

        // Mock the Runtime.getRuntime() behavior to simulate system commands
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            // Mock the cloning process to always succeed
            Process mockCloneProcess = mock(Process.class);
            Process mockMvnProcess = mock(Process.class);
            // Simulate successful cloning with exit code 0

            // Mock the Maven (mvn test) process to simulate a build success
            when(mockCloneProcess.waitFor()).thenReturn(0);
            when(mockMvnProcess.waitFor()).thenReturn(0);

            // Simulate mvn test output with BUILD SUCCESS
            InputStream mvnInputStream = new ByteArrayInputStream("BUILD SUCCESS".getBytes());
            when(mockMvnProcess.getInputStream()).thenReturn(mvnInputStream);// Output contains "BUILD SUCCESS"

            // Mock Runtime to return our mocked clone process
            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(anyString())).thenReturn(mockCloneProcess);

            // Mock static Runtime.getRuntime method
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            // Mock ProcessBuilder using spy
            ProcessBuilder spyProcessBuilder = spy(new ProcessBuilder("mvn", "test"));
            when(spyProcessBuilder.start()).thenReturn(mockMvnProcess);

            // Execute BuildManager.runBuild() with mocked dependencies
            boolean result = BuildManager.runBuild("testOwner", "testRepo", "main");
            // Assert that the build succeeds and returns true
            assertTrue(result, "Build should succeed when mvn test outputs BUILD SUCCESS");
        }
    }

    @Test
    void testRunBuildFailure() throws Exception {
        // Test case: Verify that BuildManager.runBuild() returns false when the build (mvn test) fails

        // Mock the Runtime.getRuntime() behavior to simulate system commands
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            // Mock the cloning process to always succeed
            Process mockCloneProcess = mock(Process.class);
            Process mockMvnProcess = mock(Process.class);

            when(mockCloneProcess.waitFor()).thenReturn(0);
            when(mockMvnProcess.waitFor()).thenReturn(1);

            // Simulate mvn test output without BUILD SUCCESS
            InputStream mvnInputStream = new ByteArrayInputStream("BUILD FAILURE".getBytes());
            when(mockMvnProcess.getInputStream()).thenReturn(mvnInputStream);

            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(anyString())).thenReturn(mockCloneProcess);

            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            // Mock ProcessBuilder using spy
            ProcessBuilder spyProcessBuilder = spy(new ProcessBuilder("mvn", "test"));
            when(spyProcessBuilder.start()).thenReturn(mockMvnProcess);

            // Execute BuildManager.runBuild() with mocked dependencies
            boolean result = BuildManager.runBuild("testOwner", "testRepo", "main");
            // Assert that the build fails and returns false
            assertTrue(result, "Build should fail when mvn test outputs BUILD FAILURE");
        }
    }
}

