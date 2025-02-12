package com.group21.ci;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.StringReader;

import static org.mockito.Mockito.*;

class WebhookHandlerTest {

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    void testProcessWebhook_Success() throws Exception {
        // a example JSON payload simulating a GitHub webhook event
        String payload = "{ " +
                "\"repository\": { \"name\": \"example-repo\", \"owner\": { \"login\": \"example-owner\" } }, " +
                "\"head_commit\": { \"id\": \"abc123\" }, " +
                "\"ref\": \"refs/heads/main\" " +
                "}";

        // Mock request reader to return our test JSON payload
        BufferedReader reader = new BufferedReader(new StringReader(payload));
        when(mockRequest.getReader()).thenReturn(reader);

        // Mock JobQueue's static method
        try (MockedStatic<JobQueue> mockedJobQueue = mockStatic(JobQueue.class)) {
            WebhookHandler.processWebhook(mockRequest);

            // verify that JobQueue.addJob() was called with expected parameters, meaning test passes
            mockedJobQueue.verify(() -> JobQueue.addJob("example-owner", "example-repo", "abc123", "main"));
        }
    }

    @Test
    void testProcessWebhook_InvalidJson() throws Exception {
        // example invalid JSON payload
        String payload = "INVALID_JSON";

        // Mock request reader
        BufferedReader reader = new BufferedReader(new StringReader(payload));
        when(mockRequest.getReader()).thenReturn(reader);

        // Mock JobQueue (to ensure it’s not called)
        try (MockedStatic<JobQueue> mockedJobQueue = mockStatic(JobQueue.class)) {
            WebhookHandler.processWebhook(mockRequest);

            // Verify JobQueue.addJob() was never called due to invalid JSON, meaning test passes
            mockedJobQueue.verifyNoInteractions();
        }
    }

    @Test
    void testProcessWebhook_MissingFields() throws Exception {
        // JSON missing "head_commit" and "ref"
        String payload = "{ " +
                "\"repository\": { \"name\": \"example-repo\", \"owner\": { \"login\": \"example-owner\" } } " +
                "}";

        BufferedReader reader = new BufferedReader(new StringReader(payload));
        when(mockRequest.getReader()).thenReturn(reader);

        try (MockedStatic<JobQueue> mockedJobQueue = mockStatic(JobQueue.class)) {
            WebhookHandler.processWebhook(mockRequest);

            // Ensure JobQueue.addJob() was never called due to missing fields
            mockedJobQueue.verifyNoInteractions();
        }
    }
}
