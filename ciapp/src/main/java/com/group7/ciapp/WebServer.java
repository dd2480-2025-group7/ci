package com.group7.ciapp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.group7.ciapp.Project;
import org.json.JSONObject;
import java.util.stream.Collectors;

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook
 * See the Jetty documentation for API documentation of those classes.
 */
public class WebServer extends AbstractHandler {
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");

        if ("/".equals(target) && "GET".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("CI Server is up and running");
        } else if ("/webhook".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
            handleWebhook(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("<img src='https://http.cat/404' alt='404 not found' />");
        }
        // response.setStatus(HttpServletResponse.SC_OK);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        // response.getWriter().println("CI job done");

        baseRequest.setHandled(true);

    }

    private void handleWebhook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get event type from environment variable
        String eventType = request.getHeader("X-GitHub-Event");

        // check if event is push
        if (eventType.equals("push")) {
            // get payload from request, especially "repository" and "after"
            // https://docs.github.com/en/webhooks/webhook-events-and-payloads

            // get posted data in body as JSON
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JSONObject json = new JSONObject(body);

            // print body and print json
            // System.out.println(body);
            // System.out.println(json);

            String gitUrl = json.getJSONObject("repository").getString("clone_url");
            String commitHash = json.getString("after");

            // TODO: not hard code the repository URL
            if (!gitUrl.equals("https://github.com/vilhelmprytz/dd2480-ci.git")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                response.getWriter().println("Bad request, not supported repository");
                return;
            }

            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            response.getWriter().println("Push event received");

            // use storebuildresult to tell github that we've started working on the build
            // of the commit

            // process the webhook asynchrounously

            new Thread(() -> {
                // create new project object
                Project project = new Project(gitUrl, commitHash);
                Boolean isSuccess = project.start();

                // store build result
                // StoreBuildResult storeBuildResult = new StoreBuildResult();
                // storeBuildResult.storeBuildResult(isSuccess, commitHash);
            }).start();
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Bad request, not supported event type");
        }
    }
}
