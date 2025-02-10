package com.group21.ci;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * StatusReporter updates the GitHub commit status after a CI job execution.
 * - Sends "success" or "failure" status to the GitHub API.
 * - Associates the CI result with a specific commit.
 */
public class StatusReporter {
    // 🔒 GitHub personal access token (MUST be stored securely, e.g., in
    // environment variables)
    private static final String GITHUB_TOKEN = "your_github_personal_access_token";

    /**
     * Sends a commit status update to GitHub.
     *
     * @param repoOwner The owner of the GitHub repository (e.g., "username").
     * @param repoName  The name of the GitHub repository (e.g., "repo-name").
     * @param commitSHA The SHA of the commit being reported.
     * @param success   Whether the CI build was successful or failed.
     */
    public static void sendStatus(String repoOwner, String repoName, String commitSHA, boolean success) {
        try {
            String token = System.getenv("GITHUB_TOKEN");
            if (token == null || token.isEmpty()) {
                System.out.println("ERROR: GITHUB_TOKEN is not set or is empty!");
                return;
            }

            String statusUrl = "https://api.github.com/repos/" + repoOwner + "/" + repoName + "/statuses/" + commitSHA;
            String statusPayload = "{ \"state\": \"" + (success ? "success" : "failure") + "\", "
                    + "\"description\": \"CI Build " + (success ? "passed" : "failed") + "\", "
                    + "\"context\": \"ci-server\"}";

            HttpURLConnection connection = (HttpURLConnection) new URL(statusUrl).openConnection();
            connection.setRequestMethod("POST");

            // Add standard GitHub headers
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Authorization", "token " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Debugging output
            System.out.println("Updating status for: " + repoOwner + "/" + repoName);
            System.out.println("Commit SHA: " + commitSHA);
            System.out.println("API URL: " + statusUrl);
            System.out.println("Payload: " + statusPayload);
            System.out.println("Authorization: token XXXXXX (Length: " + token.length() + ")");

            // Send the payload
            OutputStream os = connection.getOutputStream();
            os.write(statusPayload.getBytes());
            os.flush();
            os.close();

            // Read response from GitHub API
            int responseCode = connection.getResponseCode();
            System.out.println("GitHub API Response Code: " + responseCode);

            BufferedReader reader;
            if (responseCode >= 400) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("GitHub API Response: " + line);
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}