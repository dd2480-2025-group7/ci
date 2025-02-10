package com.group7.ciapp;

import java.io.File;
import java.io.IOException;

/**
 * Interface for starting a process.
 * 
 * Implementations of this interface should start a process in the given
 * directory.
 * 
 * @see ProcessExecutor
 */
public class MavenProcessExecutor implements ProcessExecutor {
    /**
     * Starts a Maven process in the given directory.
     * 
     * @param directory (File) The directory in which to start the process.
     * @return (Process) The started process.
     * @throws IOException
     */
    @Override
    public Process startProcess(File directory) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("mvn", "package");
        builder.directory(directory);
        builder.redirectErrorStream(true);
        return builder.start();
    }
}
