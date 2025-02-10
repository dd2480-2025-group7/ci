package com.group7.ciapp;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jgit.api.Git;
import org.json.JSONObject;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This class is responsible for handling incoming HTTP requests.
 * It is used to handle the webhook from GitHub and start the CI process.
 * It also handles the root path to check if the server is running.
 */
public class WebServer extends AbstractHandler {
    private static ConfigReader configReader;
    private static Database database;

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

        if (database == null) {
            database = new Database(System.getenv("DATABASE_PATH"));
            database.createTable();
        }
    }

    public void renderTemplate(HttpServletResponse response, String templateName, Map<String, Object> data)
            throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setClassLoaderForTemplateLoading(WebServer.class.getClassLoader(), "templates");
        cfg.setDefaultEncoding("UTF-8");

        Template template = cfg.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        try {
            template.process(data, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(writer.toString());
    }

    /**
     * Handle incoming HTTP requests
     * 
     * @param target      The target of the request
     * @param baseRequest The base request
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @throws IOException
     * @throws ServletException
     */
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");

        if ("/".equals(target) && "GET".equalsIgnoreCase(request.getMethod())) {
            // create list of builds
            List<Build> builds = new ArrayList<>();

            // get builds from database
            builds = database.getBuilds();

            // put into data model that freemarker can use
            Map<String, Object> model = new HashMap<>();
            model.put("builds", builds);

            // render template
            renderTemplate(response, "index.ftl", model);
        } else if ("/webhook".equals(target) && "POST".equalsIgnoreCase(request.getMethod())) {
            handleWebhook(request, response);
        } else if (target.matches("/build/\\d+")) {
            // get the build ID from the URL
            String[] parts = target.split("/");
            Long id = Long.parseLong(parts[2]);

            // get the build from the database
            Build build = database.getBuild(id);

            // if build is found, render the template
            if (build != null) {
                Map<String, Object> model = new HashMap<>();
                model.put("build", build);

                // just display the log as plain text
                response.setContentType("text/plain;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(build.getBuildLog());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("<img src='https://http.cat/404' alt='404 not found' />");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("<img src='https://http.cat/404' alt='404 not found' />");
        }

        // mark the request as handled, else the server will keep waiting
        baseRequest.setHandled(true);
    }

    /**
     * Handle incoming webhook from GitHub
     * 
     * @param request  The HTTP request
     * @param response The HTTP response
     * @throws IOException
     */
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

            // process the webhook asynchrounously, we don't want to block the server
            new Thread(() -> {
                // create new store build result object
                String jwt = TokenGetter.token(System.getenv("APP_ID"));
                StoreBuildResult sbr = new StoreBuildResult(jwt);

                Long checkId = null;

                // set status to building on GitHub

                try {
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

                // run tests and get result
                String path = project.cloneRepo();
                Boolean isSuccess = project.runMavenTests(path);
                project.deleteRepo(path);

                // store build result in database
                database.insertBuild(checkId, commitHash, project.getLog());

                // set status to complete on GitHub

                try {
                    sbr.setStatusComplete(commitHash, owner, repo, isSuccess, checkId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (org.apache.hc.core5.http.ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            // if event is not push, return bad request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Bad request, not supported event type");
        }
    }
}
