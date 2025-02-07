package com.group7.ciapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Reads the configuration file and loads the repositories.
 */
public class ConfigReader {
    private Properties properties = new Properties();
    private List<Repository> repositories = new ArrayList<>();

    /**
     * Reads the configuration file.
     */
    public ConfigReader() {
        // get config file path
        String configFilePath = System.getProperty("config.file");

        // if defined by user, read the config file
        if (configFilePath != null) {
            try (FileInputStream input = new FileInputStream(configFilePath)) {
                properties.load(input);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load external config file: " + configFilePath, ex);
            }
        } else {
            // otherwise, read the default config file from resources
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    // this should never happen, but always good to check just in case
                    throw new RuntimeException("Default config file not found in resources.");
                }
                properties.load(input);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load default config file", ex);
            }
        }
    }

    /**
     * Loads the repositories from the configuration file.
     */
    public void loadRepositories() {
        String repoList = properties.getProperty("repos.list", "");
        for (String entry : repoList.split(",")) {
            String[] parts = entry.split(";");
            if (parts.length == 2) {
                repositories.add(new Repository(parts[0], parts[1]));
            }
        }
    }

    /**
     * Returns the list of repositories.
     * 
     * @return the list of repositories
     */
    public List<Repository> getRepositories() {
        return repositories;
    }
}

/**
 * Represents a repository.
 */
class Repository {
    private final String url;
    private final String pomPath;

    /**
     * Creates a new repository.
     * 
     * @param url
     * @param pomPath
     */
    public Repository(String url, String pomPath) {
        this.url = url;
        this.pomPath = pomPath;
    }

    /**
     * Returns the URL of the repository.
     * 
     * @return the URL of the repository
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the path to the POM file of the repository.
     * 
     * @return the path to the POM file of the repository
     */
    public String getPomPath() {
        return pomPath;
    }
}
