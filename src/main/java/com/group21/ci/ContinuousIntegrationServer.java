package com.group21.ci;


import com.group21.ci.dao.TestResultDAO;
import com.group21.ci.entity.TestResultEntity;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ajax.JSON;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Continuous Integration Server that listens for webhooks, serves build history, and serves a static HTML page.
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    private static final TestResultDAO testResultDAO = new TestResultDAO();


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


        // Set response content type and status
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);


        // Handle webhook request
        if ("/webhook".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
            WebhookHandler.processWebhook(request);
            response.getWriter().println("Webhook processed");
            return;
        }


        // Handle API request for build history
        if ("/api/history".equals(target)) {
            try {
                List<TestResultEntity> results = testResultDAO.getAllTestResults();
                String json = new JSON().toJSON(results);


                response.setContentType("application/json");
                response.getWriter().println(json);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("{\"error\": \"Failed to fetch build history\"}");
            }
            return;
        }


        // Serve history page
        if ("/history".equals(target)) {
            try {
                Path historyPath = Paths.get(System.getProperty("user.dir"), "./src/main/resources/static/history.html");
//            String historyPage = Files.readString(Path.of("resources/static/history.html"));
                String historyPage = new String(Files.readAllBytes(historyPath));
                response.setContentType("text/html");
                response.getWriter().println(historyPage);
                handleBuildHistory(response);
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("{\"error\": \"Failed to load history page\"}");
            }
            return;
        }


        // Default response for unknown endpoints
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().println("{\"error\": \"Invalid endpoint\"}");
    }


    /**
     * Serves a static HTML page displaying build history.
     */
    private void handleBuildHistoryPage(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");


        String htmlFilePath = "src/main/resources/history.html"; // Path to your HTML file
        if (Files.exists(Paths.get(htmlFilePath))) {
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)));
            response.getWriter().println(htmlContent);
        } else {
            response.getWriter().println("<html><body><h2>History Page Not Found</h2></body></html>");
        }
    }


    /**
     * Retrieves and returns the list of past builds as JSON.
     */
    private void handleBuildHistory(HttpServletResponse response) throws IOException {
        List<TestResultEntity> buildResults = testResultDAO.getAllTestResults();
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


    /**
     * Retrieves and returns details of a specific build as JSON.
     */
    private void handleBuildDetails(String commitSha, HttpServletResponse response) throws IOException {
        TestResultEntity result = testResultDAO.getResultByCommit(commitSha);
        if (result == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("{\"error\": \"Build not found\"}");
            return;
        }


        response.setContentType("text/html;charset=utf-8");
        String htmlResponse = "<html><body><h2>Build Details</h2>" +
                "<p><b>Commit:</b> " + result.getCommitSha() + "</p>" +
                "<p><b>Status:</b> " + result.getStatus().toString() + "</p>" +
                "<p><b>Timestamp:</b> " + result.getTimestamp() + "</p>" +
//                "<pre>" + result.getLogs() + "</pre>" +
                "</body></html>";
        response.getWriter().println(htmlResponse);
    }


    private String generateHistoryPage() {
        List<TestResultEntity> testResults = testResultDAO.getAllTestResults();
        StringBuilder html = new StringBuilder("<html><head><title>Build History</title></head><body>");


        html.append("<h1>Build History</h1>");
        html.append("<table border='1'><tr><th>Commit ID</th><th>Build Date</th><th>Status</th><th>Logs</th></tr>");


        for (TestResultEntity result : testResults) {
            html.append("<tr>")
                    .append("<td>").append(result.getCommitSha()).append("</td>")
                    .append("<td>").append(result.getTimestamp()).append("</td>")
                    .append("<td>").append(result.getStatus()).append("</td>")
                    .append("<td><a href='/build/").append(result.getId()).append("'>View Logs</a></td>")
                    .append("</tr>");
        }


        html.append("</table></body></html>");
        return html.toString();
    }


}
