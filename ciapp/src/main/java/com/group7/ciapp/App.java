package com.group7.ciapp;

import org.eclipse.jetty.server.Server;

/**
 * App
 * 
 * This class is responsible for starting the server and listening for incoming
 * requests.
 */
public class App {

    /**
     * Starts the server and listens for incoming requests
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new WebServer());
        server.start();
        server.join();
    }
}
