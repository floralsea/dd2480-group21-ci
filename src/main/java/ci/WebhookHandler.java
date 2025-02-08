package ci;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONObject;

/**
 * Handles incoming webhook events from GitHub.
 * This class processes HTTP requests and extracts relevant repository
 * information.
 */
public class WebhookHandler {

    /**
     * Processes a webhook request from GitHub.
     * Extracts repository details and adds a CI job to the job queue.
     * 
     * @param request The HTTP request containing the webhook payload.
     */
    public static void processWebhook(HttpServletRequest request) {
        try {
            // Read the incoming request payload
            StringBuilder payload = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                payload.append(line);
            }

            // Parse JSON payload from GitHub webhook
            JSONObject json = new JSONObject(payload.toString());

            // Extract repository URL and commit SHA from webhook data
            String repoUrl = json.getJSONObject("repository").getString("clone_url");
            String commitSHA = json.getJSONObject("head_commit").getString("id");

            // Log received webhook data
            System.out.println("Webhook received for repo: " + repoUrl);

            // Add a new job to the queue for processing the CI pipeline
            JobQueue.addJob(repoUrl, commitSHA);

        } catch (IOException e) {
            // Print error stack trace if reading the request fails
            e.printStackTrace();
        }
    }
}