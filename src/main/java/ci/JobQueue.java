package ci;

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
     * @param repoUrl   The URL of the repository where the commit was pushed.
     * @param commitSHA The commit SHA for which the CI job is triggered.
     */
    // public static void addJob(String repoUrl, String commitSHA) {
    // // Extract repository owner and name from the repo URL
    // String repoOwner = extractRepoOwner(repoUrl);
    // String repoName = extractRepoName(repoUrl);

    // // Create a new job and add it to the queue
    // BuildJob job = new BuildJob(repoUrl, commitSHA, repoOwner, repoName);
    // queue.add(job);

    // // Start a new worker thread to process this job
    // new Thread(new BuildWorker(job)).start();
    // }

    // /**
    // * Represents a CI job containing repository details and commit SHA.
    // */
    // public static class BuildJob {
    // String repoUrl; // URL of the repository
    // String commitSHA; // SHA of the commit being built
    // String repoOwner; // Owner of the repository
    // String repoName; // Repository name

    // /**
    // * Constructor for BuildJob.
    // *
    // * @param repoUrl The repository URL.
    // * @param commitSHA The commit SHA to build.
    // * @param repoOwner The owner of the repository.
    // * @param repoName The name of the repository.
    // */
    // public BuildJob(String repoUrl, String commitSHA, String repoOwner, String
    // repoName) {
    // this.repoUrl = repoUrl;
    // this.commitSHA = commitSHA;
    // this.repoOwner = repoOwner;
    // this.repoName = repoName;
    // }
    // }

    public static void addJob(String repoOwner, String repoName, String commitSHA) {
        BuildJob job = new BuildJob(repoOwner, repoName, commitSHA);
        queue.add(job);
        new Thread(new BuildWorker(job)).start();
    }

    public static class BuildJob {
        String repoOwner;
        String repoName;
        String commitSHA;

        public BuildJob(String repoOwner, String repoName, String commitSHA) {
            this.repoOwner = repoOwner;
            this.repoName = repoName;
            this.commitSHA = commitSHA;
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