package com.group7.ciapp;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jgit.api.Git;
import org.json.JSONObject;

/**
 * This class is responsible for handling incoming HTTP requests.
 * It is used to handle the webhook from GitHub and start the CI process.
 * It also handles the root path to check if the server is running.
 */
public class WebServer extends AbstractHandler {
    private static ConfigReader configReader;

    /**
     * Add a shutdown hook to handle cleanup when server is stopped
     * Set configReader if null, and load repositories
     */
    public WebServer() {
        // add shutdown hook to handle cleanup when server is stopped
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook running...");
            Git.shutdown();
            System.out.println("JGit Shutdown complete");
        }));

        if (configReader == null) {
            configReader = new ConfigReader();
            configReader.loadRepositories();
        }
    }

    /**
     * Handle incoming HTTP requests.
     * 
     * @param target      (String) The target of the request.
     * @param baseRequest (Request) The base request.
     * @param request     (HttpServletRequest) The HTTP request.
     * @param response    (HttpServletResponse) The HTTP response.
     */
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
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

            // mark the request as handled, else the server will keep waiting
            baseRequest.setHandled(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle incoming webhook from GitHub.
     * 
     * @param request  (HttpServletRequest) The HTTP request
     * @param response (HttpServletResponse) The HTTP response
     * @throws IOException If an input or output exception occurs.
     */
    private void handleWebhook(HttpServletRequest request, HttpServletResponse response) {
        try {
            // get event type from environment variable
            String eventType = request.getHeader("X-GitHub-Event");

            // check if event is push
            if (eventType.equals("push")) {
                // get payload from request, especially "repository" and "after"
                // https://docs.github.com/en/webhooks/webhook-events-and-payloads

                // get posted data in body as JSON
                String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                JSONObject json = new JSONObject(body);

                // get git URL, commit hash, owner, and repo name
                String gitUrl = json.getJSONObject("repository").getString("clone_url");
                String commitHash = json.getString("after");
                String owner = json.getJSONObject("repository").getJSONObject("owner").getString("name");
                String repo = json.getJSONObject("repository").getString("name");

                // check if repository URL is in the list of repositories
                boolean found = false;
                for (Repository repository : configReader.getRepositories()) {
                    if (repository.getUrl().equals(gitUrl)) {
                        found = true;
                        break;
                    }
                }

                // if repository is not found, return bad request
                if (!found) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                    response.getWriter().println("Bad request, not supported repository");
                    return;
                }

                // return accepted status, so github knows we received the webhook
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.getWriter().println("Push event received");

                // Process the webhook asynchrounously, we don't want to block the server
                new Thread(() -> newThread(commitHash, owner, repo, gitUrl)).start();
            } else {
                // if event is not push, return bad request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Bad request, not supported event type");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The code running in the thread which processes the webhook.
     * 
     * @param commitHash (String) The hash of the commit being tested.
     * @param owner      (String) The owner of the repo.
     * @param repo       (String) The GitHub repo that is being used.
     * @param gitUrl     (String) The URL of the repository.
     * @throws IOException                             If an input or output
     *                                                 exception occurs.
     * @throws java.text.ParseException                If a parse exception occurs.
     * @throws org.apache.hc.core5.http.ParseException If a parse exception occurs.
     * @throws Exception                               If an exception occurs.
     */
    private void newThread(String commitHash, String owner, String repo, String gitUrl) {
        String jwt;
        StoreBuildResult sbr;
        Long checkId;

        try {
            // create new store build result object
            jwt = TokenGetter.token(System.getenv("APP_ID"));
            sbr = new StoreBuildResult(jwt);
            checkId = null;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            // set status to building on GitHub
            checkId = sbr.setStatusBuilding(commitHash, owner, repo);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        } catch (org.apache.hc.core5.http.ParseException e) {
            e.printStackTrace();
        }

        // create new project object with git URL and commit hash
        Project project = new Project(gitUrl, commitHash, checkId);
        String path = null;

        try {
            // run tests and get result
            path = project.cloneRepo();
            Boolean isSuccess = project.runMavenTests(path);

            // set status to complete on GitHub
            sbr.setStatusComplete(commitHash, owner, repo, isSuccess, checkId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.hc.core5.http.ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                project.deleteRepo(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
