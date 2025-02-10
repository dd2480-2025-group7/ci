package com.group7.ciapp;

import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

/**
 * This class is responsible for storing the build result and sending it to the
 * GitHub API.
 */
public class StoreBuildResult {
    private String github_token;

    /**
     * Get github token from environment variable.
     * 
     * @param jwt (String) The GitHub token.
     * @throws Exception if the GitHub token is not found.
     */
    public StoreBuildResult(String jwt) throws Exception {
        // Get the environment variable
        this.github_token = jwt;

        // Check if the variable is set
        if (this.github_token == null) {
            throw new Exception("No GitHub token found.");
        }
    }

    /**
     * Post request to GitHub API to flag commit as success or failure.
     * 
     * @param commitHash (String) The hash of the commit being tested.
     * @param owner      (String) The owner of the repo.
     * @param repo       (boolean) The GitHub repo that is being used.
     * @return (int) The check ID of the check run.
     * @throws Exception if an error occurs while sending the request.
     */
    public Long setStatusBuilding(String commitHash, String owner, String repo)
            throws Exception {
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

        // Get JSON response as org.JSON.JSONObject
        String responseString = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponse = new org.json.JSONObject(responseString);
        Long checkID = jsonResponse.getLong("id");
        return checkID;
    }

    /**
     * Sends a HTTP POST request containing the status of the build and tests.
     * If successful, set "conclusion" to success. Otherwise, set "conclusion" to
     * failure.
     * 
     * @param commitHash (String) The hash of the commit being tested.
     * @param owner      (String) The owner of the repo.
     * @param repo       (boolean) The GitHub repo that is being used.
     * @param isSuccess  (boolean) Says whether the tests passsed or not.
     * @param checkID    (Long) The check ID of the check run.
     * @throws Exception if an error occurs while sending the request.
     */
    public void setStatusComplete(String commitHash, String owner, String repo, boolean isSuccess, Long checkID)
            throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPatch request = new HttpPatch(
                String.format("https://api.github.com/repos/%s/%s/check-runs/%d", owner, repo, checkID));
        request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.github_token);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        String jsonBody = "{"
                + "\"status\": \"completed\","
                + "\"conclusion\": \"" + ((isSuccess) ? "success" : "failure") + "\","
                + "\"output\": {"
                + "   \"title\": \"Build " + ((isSuccess) ? "success" : "failure") + "\","
                + "   \"summary\": \"The build and tests passed successfully.\""
                + "}"
                + "}";
        request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = client.execute(request);

        // Get JSON response as org.JSON.JSONObject
        String responseString = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponse = new org.json.JSONObject(responseString);
    }
}
