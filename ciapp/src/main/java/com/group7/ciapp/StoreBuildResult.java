package com.group7.ciapp;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import java.io.IOException;
import java.text.ParseException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;

public class StoreBuildResult {
    private String github_token;

    public StoreBuildResult() {
        // Get the environment variable
        this.github_token = System.getenv("GITHUB_ACCESS_TOKEN");

        // Check if the variable is set
        if (this.github_token == null) {
            // very much error
        }
    }

    /**
     * post request to GitHub API to flag commit as success or failure
     * 
     * @param commitHash (String) The hash of the commit being tested
     * @param owner      (String) The owner of the repo.
     * @param repo       (boolean) The GitHub repo that is being used.
     * @return
     * @throws IOException
     * @throws org.apache.hc.core5.http.ParseException
     */
    public void setStatusBuilding(String commitHash, String owner, String repo)
            throws IOException, ParseException, org.apache.hc.core5.http.ParseException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(String.format("https://api.github.com/repos/%s/%s/check-runs", owner, repo));
        request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.github_token);
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.addHeader(HttpHeaders.USER_AGENT, "Apache HttpClient");
        String jsonBody = "{"
                + "\"name\": \"CI Build\","
                + "\"head_sha\": \"" + commitHash + "\","
                + "\"status\": \"in_progress\","
                + "\"output\": {"
                + "   \"title\": \"Testing\","
                + "   \"summary\": \"The build is currently being tested.\""
                + "}"
                + "}";
        request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = client.execute(request);

        // Get JSON response
        String jsonResponse = EntityUtils.toString(response.getEntity());
        System.out.println(jsonResponse);
    }

    /**
     * Sends a HTTP POST request containing the status of the build and tests.
     * If successful, set "conclusion" to success. Otherwise, set "conclusion" to
     * failure.
     * 
     * TODO: Make a patch request instead of a post request
     * 
     * @param commitHash (String) The hash of the commit being tested
     * @param owner      (String) The owner of the repo.
     * @param repo       (boolean) The GitHub repo that is being used.
     * @param isSuccess  (boolean) Says whether the tests passsed or not.
     * @return
     * @throws IOException
     */
    public void setStatusComplete(String commitHash, String owner, String repo, boolean isSuccess) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(String.format("https://api.github.com/repos/%s/%s/check-runs", owner, repo));
        request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.github_token);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        String jsonBody = "{"
                + "\"name\": \"CI Build\","
                + "\"head_sha\": \"" + commitHash + "\","
                + "\"status\": \"completed\","
                + "\"conclusion\": \"" + isSuccess + "\","
                + "\"output\": {"
                + "   \"title\": \"Build Passed\","
                + "   \"summary\": \"The build and tests passed successfully.\""
                + "}"
                + "}";
        request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = client.execute(request);

        // print response data in JSON
        System.out.println(response);
    }
    // store build logs in a file
}
