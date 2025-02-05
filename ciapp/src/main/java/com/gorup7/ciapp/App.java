package com.group7.ciapp;

import org.eclipse.jetty.server.Server;
import com.group7.ciapp.ContinuousIntegrationServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
