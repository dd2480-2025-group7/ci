package com.group7.ciapp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WebServerTest {
    private WebServer webServer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Request baseRequest;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        webServer = new WebServer();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        baseRequest = mock(Request.class);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    /**
     * Test that the server responds with a 200 OK status code and the correct
     * message when the root path is requested.
     *
     * @throws Exception
     */
    @Test
    public void testHandleIndex() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/");

        webServer.handle("/", baseRequest, request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("CI Server is up and running"),
                "Expected response to contain 'CI Server is up and running' but got: " + responseWriter.toString());
    }

    /**
     * Test that the server responds with a 202 accepted status code and the correct
     * message when a push event is received.
     *
     * @throws Exception
     */
    @Test
    public void testHandleWebhookPush() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/webhook");
        when(request.getHeader("X-GitHub-Event")).thenReturn("push");

        String requestBody = "{" +
                "\"repository\": {\"clone_url\": \"https://github.com/dd2480-2025-group7/ci.git\", \"name\": \"repo\", \"owner\": {\"name\": \"dd2480-2025-group7\"}},"
                +
                "\"after\": \"1ec276497c3e5879591cafb00f63df6b81caeba4\"" +
                "}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        webServer.handle("/webhook", baseRequest, request, response);

        assertTrue(responseWriter.toString().contains("Push event received"),
                "Expected response to contain 'Push event received' but got: " + responseWriter.toString());

        verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
    }

    /**
     * Test that the server responds with a 400 bad request status code and the
     * correct message when an invalid event type is received.
     *
     * @throws Exception
     */
    @Test
    public void testHandleWebhookWithInvalidEvent() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/webhook");
        when(request.getHeader("X-GitHub-Event")).thenReturn("pull_request");

        webServer.handle("/webhook", baseRequest, request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Bad request, not supported event type"),
                "Expected response to contain 'Bad request, not supported event type' but got: "
                        + responseWriter.toString());
    }

    /**
     * Test that the server responds with a 400 bad request status code and the
     * correct message when an unsupported repository is received.
     *
     * @throws Exception
     */
    @Test
    public void testHandleWebhookWithUnsupportedRepository() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/webhook");
        when(request.getHeader("X-GitHub-Event")).thenReturn("push");

        String requestBody = "{" +
                "\"repository\": {\"clone_url\": \"https://github.com/dd2480-2025-group7/launch_interceptor_program.git\", \"name\": \"repo\", \"owner\": {\"name\": \"dd2480-2025-group7\"}},"
                +
                "\"after\": \"1ec276497c3e5879591cafb00f63df6b81caeba4\"" +
                "}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        webServer.handle("/webhook", baseRequest, request, response);

        assertTrue(responseWriter.toString().contains("Bad request, not supported repository"),
                "Expected response to contain 'Bad request, not supported repository' but got: "
                        + responseWriter.toString());

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Test that the server responds with a 404 not found status code and the
     * correct message when an invalid path is requested.
     *
     * @throws Exception
     */
    @Test
    public void testHandleNotFound() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/invalid");

        webServer.handle("/invalid", baseRequest, request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(responseWriter.toString().contains("404"),
                "Expected response to contain '404' but got: " + responseWriter.toString());
    }
}
