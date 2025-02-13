package com.group21.ci;

import com.group21.ci.dao.TestResultDAO;
import com.group21.ci.entity.TestResultEntity;
//import com.google.gson.Gson;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Continuous Integration Server
 * - Handles webhook events
 * - Serves the history of past builds
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    private static final TestResultDAO testResultDAO = new TestResultDAO();
    private static final JSON json = new JSON(); // JSON Serializer

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());

        server.start();
        System.out.println("CI Server running on port 8080...");
        server.join();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        baseRequest.setHandled(true);

        // Handle webhook request
        if ("/webhook".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
            WebhookHandler.processWebhook(request);
            response.getWriter().println("Webhook processed");
            return;
        }

        // Handle API request for build history (All builds)
        if ("/api/history".equals(target)) {
            handleBuildHistory(response);
            return;
        }

        // Handle API request for a specific build details
        if (target.startsWith("/api/history/")) {
            String commitSha = target.substring("/api/history/".length());
            handleBuildDetails(response, commitSha);
            return;
        }

        // Serve the build history HTML page
        if ("/history".equals(target)) {
            serveHistoryPage(response);
            return;
        }
//        if ("/history".equals(target)) {
//            try {
//                Path historyPath = Paths.get(System.getProperty("user.dir"), "./src/main/resources/static/history.html");
//                String historyPage = new String(Files.readAllBytes(historyPath));
//
//                response.setContentType("text/html");
//                response.getWriter().println(historyPage);
//            } catch (IOException e) {
//                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                response.getWriter().println("{\"error\": \"Failed to load history page\"}");
//            }
//            return;
//        }

        // Serve a specific build details page
        if (target.startsWith("/builds/")) {
            String commitSha = target.substring("/builds/".length());
            serveBuildDetailsPage(response, commitSha);
            return;
        }

        // Default response for unknown endpoints
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().println("{\"error\": \"Invalid endpoint\"}");
    }

    /**
     * Returns all past builds in JSON format.
     */
//    private void handleBuildHistory(HttpServletResponse response) throws IOException {
//        try {
//            List<TestResultEntity> results = testResultDAO.getAllTestResults();
//            response.setContentType("application/json");
//            response.getWriter().println(json.toJSON(results));
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.getWriter().println("{\"error\": \"Failed to fetch build history\"}");
//        }
//    }

    /**
     * Returns details for a specific build in JSON format.
     */
    private void handleBuildDetails(HttpServletResponse response, String commitSha) throws IOException {
        try {
            TestResultEntity result = testResultDAO.getTestResultById(Integer.parseInt(commitSha));
            if (result != null) {
                response.setContentType("application/json");
                response.getWriter().println(json.toJSON(result));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("{\"error\": \"Build not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"Failed to fetch build details\"}");
        }
    }

    /**
     * Serves the HTML page that displays build history.
     */
    private void serveHistoryPage(HttpServletResponse response) throws IOException {
        try {
            Path historyPath = Paths.get(System.getProperty("user.dir"), "./src/main/resources/static/history.html");
            String historyPage = new String(Files.readAllBytes(historyPath));
            response.setContentType("text/html");
            response.getWriter().println(historyPage);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"Failed to load history page\"}");
        }
    }

    /**
     * Serves an HTML page for a specific build's details.
     */
    private void handleBuildHistory(HttpServletResponse response) throws IOException {
        List<TestResultEntity> buildResults = testResultDAO.getAllTestResults();
        System.out.println("Building history page with " + buildResults.size() + " entries."); // 添加日志

        StringBuilder html = new StringBuilder("<html><body><h2>Build History</h2><ul>");
        for (TestResultEntity result : buildResults) {
            html.append("<li><a href='/builds/")
                    .append(result.getCommitSha())
                    .append("'>")
                    .append(result.getCommitSha())
                    .append("</a> - ")
                    .append(result.getStatus().toString())
                    .append(" - ")
                    .append(result.getTimestamp())
                    .append("</li>");
        }
        html.append("</ul></body></html>");

        response.getWriter().println(html.toString());
    }

    private void serveBuildDetailsPage(HttpServletResponse response, String commitSha) throws IOException {
        TestResultEntity result = testResultDAO.getTestResultByCommitSha(commitSha);

        if (result == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("<html><body><h2>Build Not Found</h2></body></html>");
            return;
        }

        StringBuilder html = new StringBuilder("<html><body>");
        html.append("<h2>Build Details</h2>");
        html.append("<p><strong>Commit SHA:</strong> ").append(result.getCommitSha()).append("</p>");
        html.append("<p><strong>Status:</strong> ").append(result.getStatus()).append("</p>");
        html.append("<p><strong>Timestamp:</strong> ").append(result.getTimestamp()).append("</p>");
//        html.append("<h3>Test Log:</h3><pre>").append(result.getTestLog()).append("</pre>");
        html.append("<a href='/api/history'>Back to Build History</a>");
        html.append("</body></html>");

        response.getWriter().println(html.toString());
    }


}


