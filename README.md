# dd2480-group21-ci

## 📌 Overview
This project is a simple **Continuous Integration (CI) server** that:
- Listens for **GitHub webhook events** (pushes).
- Clones the repository and runs **Maven build & tests**.
- Reports build/test results back to **GitHub commit status API**.

---

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
 | | | | |- BuildManager.java  # Executes Git operations, builds and runs tests
 | | | | |- StatusReporter.java  # Sends commit status updates
 |- test
 | |- BuildManagerTest.java  # Unit tests for BuildManager
 | |- WebhookHandlerTest.java  # Unit tests for WebhookHandler
 |- pom.xml  # Maven dependencies
 |- README.md  # Documentation
 |- start.sh  # Starts CI server
 |- deploy_ngrok.sh  # Deploys CI server with ngrok
```

## 🚀 How to Run Locally

This CI server listens for GitHub webhook events, processes builds, and updates commit statuses.  
Follow these steps to set up and run it on your local machine.

---

### **1️⃣ Prerequisites**
Ensure you have installed:
- ✅ **Java**
- ✅ **Maven** (`mvn`)
- ✅ **ngrok** (for exposing the local server)
- ✅ **GitHub Personal Access Token (PAT)** with `repo` and `workflow` permissions

---

### **2️⃣ Setup Your GitHub Token**
#### **🔹 Generate a GitHub Personal Access Token (PAT)**
1. Go to **[GitHub → Developer Settings → Personal Access Tokens](https://github.com/settings/tokens)**
2. Click **"Generate New Token"**, select:
   - ✅ `repo` (Full control of repositories)
   - ✅ `workflow` (For updating commit statuses)
3. Click **"Generate Token"** and **copy it immediately**.

### **🔹 Store the Token as an Environment Variable**
To avoid exposing your token, **store it securely**:

```sh
export GITHUB_TOKEN=your_github_token_here
```

To verify
```sh
echo $GITHUB_TOKEN
```

### 3️⃣ Start the CI Server
Run:
```sh
mvn clean install
mvn exec:java -Dexec.mainClass="com.group21.ci.ContinuousIntegrationServer"
```
The server will now listen on port 8080.

### 4️⃣ Expose the Server Using ngrok
Since GitHub webhooks need a public URL, use ngrok to expose your local server:
```sh
ngrok http 8080
```

It will generate an output like:
```
Forwarding                    https://xyz123.ngrok-free.app -> http://localhost:8080
```
Copy the https://xyz123.ngrok-free.app URL.

### 5️⃣ Set Up the GitHub Webhook
1.	Go to GitHub → Repository → Settings → Webhooks
2.	Click “Add Webhook”
3.	Set:
 - Payload URL: https://xyz123.ngrok-free.app/webhook
 - Content type: application/json
 - Trigger: Select “Just the push event”
4.	Click “Add Webhook”.

🚨 Note: Every time ngrok http 8080 runs, the URL changes. You must update the webhook manually.

### 6️⃣ Test the Webhook
1.	Push an empty commit to trigger the CI server:
```sh
git commit --allow-empty -m "test: trigger webhook"
git push origin assessment
```
🚨 Note: You should ignore "terget/" when commit, if you want to link your commit to an issue, please use issue (#15), which refers to testing the webhook.

2.	Check your CI server logs:
 - If set up correctly, the webhook triggers the CI process.
 - Your commit status updates on GitHub.

### 7️⃣ Troubleshooting
🔹 If the CI Server fails with a 401 Unauthorized error

Run:
```sh
echo $GITHUB_TOKEN
```

If it’s empty, set it again:
```sh
export GITHUB_TOKEN=your_github_token_here
```

🔹 If ngrok gives an ERR_NGROK_108 error

Run:
```sh
pkill -f ngrok
ngrok http 8080
```

#### (Optional) Automate Webhook Updates

Since ngrok generates a new URL every time, instead of manually updating the webhook, use this script to automate it:
```sh
export NGROK_URL=$(curl -s http://127.0.0.1:4040/api/tunnels | jq -r '.tunnels[0].public_url')

curl -X PATCH -H "Authorization: token $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     -d "{\"config\": {\"url\": \"$NGROK_URL/webhook\", \"content_type\": \"json\"}}" \
     https://api.github.com/repos/YOUR_GITHUB_USERNAME/YOUR_REPO_NAME/hooks/YOUR_WEBHOOK_ID
```
## Contributions

### **🔹 Team Members**
- Biming Wen @WenBiming
- Xu Zuo @floralsea
- Gustav Nordström @GustavNordstrom
- Gustav Wallin @Muppsz

### **🔹 Contribution Statements**
- **Biming Wen**: 
  - Contributed to the new feature for p+, keeping build history
  - Define `TestResult`table of relational database MySQL, holding records for each 'mvn test'
  - Implement Data Persistance Layer of the project, including
    - classes TestResultEntity and TestStatus in package entity
    - Classes TestResultDAO, HibernateUtil and HibernateTestUtil in package dao
    - Unit tests and integral tests of TestResultDAO
  
- **Xu Zuo**:
   - Set up the initial structure based on the architecture provided by Biming Wen.
   - Implemented and contributed to CI sever, BuildManager, BuildWorker, JobQueue, StatusReporter and WebhookHandler.
   - Implemented unit test of BuildManager.
   - Contributed to the new feature for p+, keeping build history.
  
- **Gustav Nordström**:
   - Implemented/contributed to the CI server being able to run tests
   - Refactored parts of the code, mainly by removing unused or obsolete code 
   - Worked on documentation and keeping it updated
   
- **Gustav Wallin** : 

## Essence State

Regarding Essence, we believe we're currently in the collaborating stage, and while progress is being made, there are still areas for improvement. Overall, collaboration is functioning well, but to reach a higher level of success, communication and effectiveness need to be further improved.
