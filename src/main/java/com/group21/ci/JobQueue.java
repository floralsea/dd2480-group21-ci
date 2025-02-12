package com.group21.ci;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * JobQueue manages CI jobs by adding them to a queue and processing them
 * asynchronously.
 * - Extracts repository owner and name from the GitHub URL.
 * - Creates a BuildJob and adds it to the queue.
 * - Starts a new BuildWorker thread to process each job.
 */
public class JobQueue {
    // A thread-safe queue to store build jobs
    private static final BlockingQueue<BuildJob> queue = new LinkedBlockingQueue<>();

    /**
     * Adds a new CI job to the queue and starts a BuildWorker to process it.
     * 
     * @param repoOwner The owner username of the repository where the commit was pushed.
     * @param repoName The name of the repository where the commit was pushed.
     * @param commitSHA The commit SHA for which the CI job is triggered.
     * @param branchName The name of the branch which the commit belongs to.
     */

    public static void addJob(String repoOwner, String repoName, String commitSHA, String branchName) {
        BuildJob job = new BuildJob(repoOwner, repoName, commitSHA, branchName);
        queue.add(job);
        new Thread(new BuildWorker(job)).start();
    }

    public static class BuildJob {
        String repoOwner;
        String repoName;
        String commitSHA;
        String branchName;

        public BuildJob(String repoOwner, String repoName, String commitSHA, String branchName) {
            this.repoOwner = repoOwner;
            this.repoName = repoName;
            this.commitSHA = commitSHA;
            this.branchName = branchName;
        }
    }

    /**
     * Extracts the repository owner from the GitHub repository URL.
     * Example: "https://github.com/owner/repo.git" -> "owner"
     * 
     * @param repoUrl The GitHub repository URL.
     * @return The repository owner.
     */
    private static String extractRepoOwner(String repoUrl) {
        return repoUrl.split("/")[3];
    }

    /**
     * Extracts the repository name from the GitHub repository URL.
     * Example: "https://github.com/owner/repo.git" -> "repo"
     * 
     * @param repoUrl The GitHub repository URL.
     * @return The repository name without the ".git" extension.
     */
    private static String extractRepoName(String repoUrl) {
        return repoUrl.split("/")[4].replace(".git", "");
    }
}