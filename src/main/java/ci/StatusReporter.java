package ci;

import java.net.HttpURLConnection;
import java.net.URL;
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
            // Construct the GitHub API URL for commit status updates
            String statusUrl = "https://api.github.com/repos/" + repoOwner + "/" + repoName + "/statuses/" + commitSHA;

            // Create the JSON payload for the status update
            String statusPayload = "{ \"state\": \"" + (success ? "success" : "failure") + "\", "
                    + "\"description\": \"CI Build " + (success ? "passed" : "failed") + "\", "
                    + "\"context\": \"ci-server\"}";

            // Open an HTTP connection to GitHub
            HttpURLConnection connection = (HttpURLConnection) new URL(statusUrl).openConnection();
            connection.setRequestMethod("POST"); // Use POST to send data
            connection.setRequestProperty("Authorization", "Bearer " + GITHUB_TOKEN); // Authenticate with GitHub
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true); // Enable output stream for sending data

            // Send the JSON payload to GitHub
            OutputStream os = connection.getOutputStream();
            os.write(statusPayload.getBytes());
            os.flush();
            os.close();

            // Log the response code from GitHub
            System.out.println("Status updated on GitHub: " + connection.getResponseCode());

        } catch (Exception e) {
            // Print error details if the request fails
            e.printStackTrace();
        }
    }
}