package com.group7.ciapp;

import org.eclipse.jetty.server.Server;

/**
 * This class is responsible for starting the server and listening for incoming
 * requests.
 */
public class App {

    /**
     * Default constructor for App.
     */
    public App() {
        // Default constructor
    }

    /**
     * Starts the server and listens for incoming requests.
     * 
     * @param args (String[]) Command line arguments.
     * @throws Exception if an error occurs while starting the server.
     */
    public static void main(String[] args) throws Exception {
        // check that required environment variables are set
        // PRIVATE_KEY_PATH and APP_ID
        if (System.getenv("PRIVATE_KEY_PATH") == null || System.getenv("APP_ID") == null) {
            System.out.println("Please set the environment variables PRIVATE_KEY_PATH and APP_ID");
            System.exit(1);
        }

        try {
            Server server = new Server(8080);
            server.setHandler(new WebServer());
            server.start();
            server.join();
        } catch (Exception e) {
            throw new Exception("Exception if an error occurs while starting the server.\n" + e);
        }

    }
}
