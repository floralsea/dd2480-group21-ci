# dd2480-group21-ci

## 📂 Project Structure

```
📦 continuous-integration-server
 |- src
 | |- main
 | | |- java
 | | | |- ci
 | | | | |- ContinuousIntegrationServer.java  # Webhook listener
 | | | | |- WebhookHandler.java  # Parses webhook events
 | | | | |- JobQueue.java  # Manages queued build jobs
 | | | | |- BuildWorker.java  # Processes CI jobs asynchronously
 | | | | |- BuildManager.java  # Executes Git operations and builds
 | | | | |- TestRunner.java  # Runs tests and stores results
 | | | | |- StatusReporter.java  # Sends commit status updates
 | | | | |- Logger.java  # Centralized logging & result storage
 |- test
 | |- BuildManagerTest.java  # Unit tests for BuildManager
 | |- WebhookHandlerTest.java  # Unit tests for WebhookHandler
 |- pom.xml  # Maven dependencies
 |- README.md  # Documentation
 |- start.sh  # Starts CI server
 |- deploy_ngrok.sh  # Deploys CI server with ngrok
```