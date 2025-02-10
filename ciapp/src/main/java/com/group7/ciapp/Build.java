package com.group7.ciapp;

public class Build {
    private Long id;
    private String commitHash;
    private String buildDate;
    private String buildLog;

    /**
     * Constructor for Build
     * 
     * @param id
     * @param commitHash
     * @param buildDate
     * @param buildLog
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
     * @return
     */
    public String getCommitHash() {
        return commitHash;
    }

    /**
     * Get the build date of the build
     * 
     * @return
     */
    public String getBuildDate() {
        return buildDate;
    }

    /**
     * Get the build log of the build
     * 
     * @return
     */
    public String getBuildLog() {
        return buildLog;
    }
}
