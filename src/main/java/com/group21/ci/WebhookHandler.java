package com.group21.ci;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONException;
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

            // Print for debug
            System.out.println("Raw Webhook Payload: " + payload.toString());
            
            // Validate required fields exist
            if (!json.has("repository") || !json.has("head_commit") || !json.has("ref")) {
                System.err.println("Invalid webhook payload: missing required fields.");
                return;  // Exit early to prevent errors
            }
            
            // Extract repository owner, name, commitSHA and branch name from webhook data,
            // and validate required fields exist

            JSONObject repository = json.getJSONObject("repository");
            if (!repository.has("name") || !repository.has("owner")) {
                System.err.println("Invalid repository data.");
                return;
            }

            JSONObject owner = repository.getJSONObject("owner");
            if (!owner.has("login")) {
                System.err.println("Invalid repository owner data.");
                return;
            }

            String repoOwner = owner.getString("login");
            String repoName = repository.getString("name");

            JSONObject headCommit = json.getJSONObject("head_commit");
            if (!headCommit.has("id")) {
                System.err.println("Invalid commit data.");
                return;
            }

            String commitSHA = headCommit.getString("id");
            String ref = json.getString("ref"); // For extracting branch name of commit
            String branchName = ref.replace("refs/heads/", "");

            // Debugging output
            System.out.println("Extracted Repo Owner: " + repoOwner);
            System.out.println("Extracted Repo Name: " + repoName);
            System.out.println("Commit SHA: " + commitSHA);
            System.out.println("Branch Name: " + branchName);

            JobQueue.addJob(repoOwner, repoName, commitSHA, branchName);

            // Log received webhook data
            // System.out.println("Webhook received for repo: " + repoUrl);

            // Add a new job to the queue for processing the CI pipeline
            // JobQueue.addJob(repoUrl, commitSHA);

        } catch (IOException e) {
            // Print error stack trace if reading the request fails
            System.err.println("Error reading request payload: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            System.err.println("Invalid JSON received: " + e.getMessage());
            e.printStackTrace();
        }
    }
}