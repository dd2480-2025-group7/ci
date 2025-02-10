package com.group7.ciapp;

import java.io.File;
import java.io.IOException;

/**
 * Interface for starting a process.
 * 
 * Implementations of this interface should start a process in the given
 * directory.
 * 
 * @see MavenProcessExecutor
 */
public interface ProcessExecutor {
    /**
     * Starts a process in the given directory.
     * 
     * @param directory (File) The directory in which to start the process.
     * @return (Process) The started process.
     * @throws IOException
     */
    Process startProcess(File directory) throws IOException;
}
