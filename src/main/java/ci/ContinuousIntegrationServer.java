package ci;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Continuous Integration Server that listens for incoming webhooks.
 * This server runs on port 8080 and processes webhook events from GitHub.
 */
public class ContinuousIntegrationServer extends AbstractHandler {

    /**
     * Main entry point for starting the CI server.
     * 
     * @param args Command line arguments (not used)
     * @throws Exception If server fails to start
     */
    public static void main(String[] args) throws Exception {
        // Create and configure Jetty server to listen on port 8080
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());

        // Start the server
        server.start();
        System.out.println("CI Server running on port 8080...");

        // Keep server running
        server.join();
    }

    /**
     * Handles incoming HTTP requests and processes GitHub webhook events.
     * 
     * @param target      The request URI
     * @param baseRequest The base request object
     * @param request     The HTTP servlet request
     * @param response    The HTTP servlet response
     * @throws IOException      If an input or output error occurs
     * @throws ServletException If a servlet error occurs
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set response content type and status
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // Mark request as handled so it doesn't propagate further
        baseRequest.setHandled(true);

        // Check if the request is for the webhook endpoint and is a POST request
        if ("/webhook".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
            WebhookHandler.processWebhook(request); // Process the webhook event
        }

        // Send response message to client
        response.getWriter().println("CI Server Received Request");
    }
}