# Deploys CI server with ngrok
#!/bin/bash

# Check if Ngrok is installed
if ! command -v ngrok &> /dev/null; then
    echo "Ngrok is not installed! Download from https://ngrok.com/download and install it."
    exit 1
fi

# Start the CI server in the background
echo "Starting Continuous Integration Server..."
java -jar target/continuous-integration-server-1.0-SNAPSHOT.jar &  
SERVER_PID=$!

# Wait for the server to start
sleep 5

# Start Ngrok and expose port 8080
echo "Starting Ngrok on port 8080..."
ngrok http 8080 > /dev/null &  
NGROK_PID=$!

# Wait for Ngrok to initialize
sleep 5

# Extract the public URL from Ngrok
NGROK_URL=$(curl -s http://127.0.0.1:4040/api/tunnels | jq -r '.tunnels[0].public_url')

if [ -z "$NGROK_URL" ]; then
    echo "Failed to fetch Ngrok URL. Is Ngrok running?"
    kill $SERVER_PID
    exit 1
fi

# Display Ngrok URL
echo "Ngrok URL: $NGROK_URL"
echo "Update your GitHub Webhook URL to: $NGROK_URL/webhook"

# Keep script running to maintain Ngrok connection
wait $SERVER_PID