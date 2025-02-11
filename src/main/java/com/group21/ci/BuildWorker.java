package com.group21.ci;

/**
 * BuildWorker is responsible for processing individual CI jobs.
 * - It retrieves a job from the JobQueue.
 * - Runs the build process (compilation and tests).
 * - Reports the result back to GitHub.
 */
public class BuildWorker implements Runnable {
    private final JobQueue.BuildJob job; // The job containing repo owner, name, and commit details

    /**
     * Constructor for BuildWorker.
     * 
     * @param job The CI job containing the repository owner, name, and commit SHA.
     */
    public BuildWorker(JobQueue.BuildJob job) {
        this.job = job;
    }

    /**
     * Runs the CI job:
     * - Clones the repository.
     * - Runs the build and tests.
     * - Reports the build status to GitHub.
     */
    @Override
    public void run() {
        System.out.println("Processing job for commit: " + job.commitSHA);

        // Run build process and tests
        boolean buildSuccess = BuildManager.runBuild(job.repoOwner, job.repoName);

        // Determine final status (pass only if both build & test succeed)
        boolean finalStatus = buildSuccess;

        // Send status update to GitHub repository with correct repository details
        StatusReporter.sendStatus(job.repoOwner, job.repoName, job.commitSHA, finalStatus);
    }
}