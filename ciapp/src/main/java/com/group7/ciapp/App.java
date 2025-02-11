package com.group7.ciapp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;

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
        if (System.getenv("PRIVATE_KEY_PATH") == null || System.getenv("APP_ID") == null
                || System.getenv("DATABASE_PATH") == null || System.getenv("BASE_URL") == null) {
            System.out.println(
                    "Please set the environment variables PRIVATE_KEY_PATH, APP_ID and DATABASE_PATH and BASE_URL");
            System.exit(1);
        }

        try {
            System.setProperty("org.eclipse.jetty.LEVEL", "INFO");
            Log.setLog(new StdErrLog());
            Log.getRootLogger().setDebugEnabled(true);

            Server server = new Server(8080);
            server.setHandler(new WebServer());
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println("Exception if an error occurs while starting the server.");
            e.printStackTrace();
        }
    }
}
