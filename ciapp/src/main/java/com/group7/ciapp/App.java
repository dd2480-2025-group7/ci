package com.group7.ciapp;

import org.eclipse.jetty.server.Server;

/**
 * This class is responsible for starting the server and listening for incoming
 * requests.
 */
public class App {

    /**
     * Starts the server and listens for incoming requests.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // check that required environment variables are set
        // PRIVATE_KEY_PATH and APP_ID
        if (System.getenv("PRIVATE_KEY_PATH") == null || System.getenv("APP_ID") == null) {
            System.out.println("Please set the environment variables PRIVATE_KEY_PATH and APP_ID");
            System.exit(1);
        }

        Server server = new Server(8080);
        server.setHandler(new WebServer());
        server.start();
        server.join();
    }
}
