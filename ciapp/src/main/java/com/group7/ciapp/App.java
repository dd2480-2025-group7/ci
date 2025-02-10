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
     * Starts the server and listens for incoming requests.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // check that required environment variables are set
        // PRIVATE_KEY_PATH and APP_ID
        if (System.getenv("PRIVATE_KEY_PATH") == null || System.getenv("APP_ID") == null
                || System.getenv("DATABASE_PATH") == null) {
            System.out.println("Please set the environment variables PRIVATE_KEY_PATH, APP_ID and DATABASE_PATH");
            System.exit(1);
        }

        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");
        Log.setLog(new StdErrLog());
        Log.getRootLogger().setDebugEnabled(true);

        Server server = new Server(8080);
        server.setHandler(new WebServer());
        server.start();
        server.join();
    }
}
