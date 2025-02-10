package com.group7.ciapp;

/**
 * This class represents a build.
 */
public class Build {
    private Long id;
    private String commitHash;
    private String buildDate;
    private String buildLog;

    /**
     * Constructor for Build
     * 
     * @param id         the ID of the build
     * @param commitHash the commit hash of the build
     * @param buildDate  the build date of the build
     * @param buildLog   the build log of the build
     */
    public Build(Long id, String commitHash, String buildDate, String buildLog) {
        this.id = id;
        this.commitHash = commitHash;
        this.buildDate = buildDate;
        this.buildLog = buildLog;
    }

    /**
     * Get the ID of the build
     * 
     * @return (Long) The ID of the build
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the commit hash of the build
     * 
     * @return (String) The commit hash of the build
     */
    public String getCommitHash() {
        return commitHash;
    }

    /**
     * Get the build date of the build
     * 
     * @return (String) The build date of the build
     */
    public String getBuildDate() {
        return buildDate;
    }

    /**
     * Get the build log of the build
     * 
     * @return (String) The build log of the build
     */
    public String getBuildLog() {
        return buildLog;
    }
}
