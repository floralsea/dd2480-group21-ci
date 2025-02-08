package ci;

/**
 * BuildWorker is responsible for processing individual CI jobs.
 * - It retrieves a job from the JobQueue.
 * - Runs the build process (compilation and tests).
 * - Reports the result back to the repository.
 */
public class BuildWorker implements Runnable {
    private final JobQueue.BuildJob job; // The job containing repo and commit details

    /**
     * Constructor for BuildWorker.
     * 
     * @param job The CI job containing the repository URL and commit SHA.
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

        // Run build process (clone repo and compile)
        boolean buildSuccess = BuildManager.runBuild(job.repoUrl);

        // Execute test suite
        boolean testSuccess = TestRunner.runTests();

        // Determine final status (pass only if both build & test succeed)
        boolean finalStatus = buildSuccess && testSuccess;

        // Send status update to GitHub repository
        StatusReporter.sendStatus("your-repo-owner", "your-repo-name", job.commitSHA, finalStatus);
    }
}