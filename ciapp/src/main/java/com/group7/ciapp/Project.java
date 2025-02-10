package com.group7.ciapp;

import org.eclipse.jgit.api.Git;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * This class is responsible for cloning a repository, checking out a specific
 * commit hash, running tests and deleting the cloned repository.
 */
public class Project {
    private String url;
    private String commitHash;
    private Git git;
    private Long checkId;
    private String path;
    private ProcessExecutor processExecutor;

    /**
     * Sets the URL, commit hash and check-ID of the repository.
     * 
     * @param url        (String) The URL to the repository.
     * @param commitHash (String) The commit hash to checkout.
     * @param checkId    (Long) The GitHub check ID.
     */
    public Project(String url, String commitHash, Long checkId) {
        this.url = url;
        this.commitHash = commitHash;
        this.checkId = checkId;
        this.path = System.getProperty("java.io.tmpdir") + "/ci-tests/";
        this.processExecutor = new MavenProcessExecutor();
    }

    /**
     * Overloaded constructor for testing purposes.
     *
     * @param url        (String) The URL to the repository.
     * @param commitHash (String) The commit hash to checkout.
     * @param checkId    (Long) The GitHub check ID.
     * @param executor   (ProcessExecutor) The process executor to use.
     */
    public Project(String url, String commitHash, Long checkId, ProcessExecutor executor) {
        this.url = url;
        this.commitHash = commitHash;
        this.checkId = checkId;
        this.path = System.getProperty("java.io.tmpdir") + "/ci-tests/";
        this.processExecutor = executor;
    }

    /**
     * Clone the repository from the given URL and checkout the given commit hash.
     * 
     * @return (String) The path to the cloned repository. Null if an error occurs.
     * @throws Exception if an error occurs while cloning the repository.
     */
    public String cloneRepo() throws Exception {
        System.out.println("Repo URL: " + url);
        System.out.println("Commit: " + commitHash);
        // Clone repo inside tmp directory
        String path = this.path + this.checkId;
        System.out.println("Cloning repository to: " + path);

        this.git = Git.cloneRepository().setURI(url).setDirectory(new File(path)).call();
        // checkout specific commit
        this.git.checkout().setName(commitHash).call();
        return path;
    }

    /**
     * Delete the cloned repository.
     * 
     * @param path (String) The path to the cloned repository.
     * @throws Exception if the given path does not exist.
     */
    public void deleteRepo(String path) throws Exception {
        if (this.git != null) {
            this.git.close();
            this.git = null;
        }
        File dir = new File(path);
        if (dir.exists()) {
            removeRecursively(dir);
        } else {
            throw new Exception("Error deleting repo: path does not exist");
        }
    }

    /**
     * Recursively remove a file or directory from the given path.
     * 
     * @param f (File) The file or directory to remove.
     */
    private static void removeRecursively(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                removeRecursively(c);
            }
        }
        f.delete();
    }

    /**
     * Run Maven tests. If all tests pass, return true, else, return false.
     * 
     * @param path (String) The path to the cloned repository.
     * @return (boolean) True if all tests pass, false otherwise.
     * @throws Exception if an error occurs while running the tests.
     */
    public boolean runMavenTests(String path) throws Exception {
        int exitcode = -1;

        try {
            File directory = new File(path + "/ciapp");
            Process process = processExecutor.startProcess(directory);

            // Log from process
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            exitcode = process.waitFor(); // wait for process to finish running tests
            in.close();

        } catch (Exception e) {
            System.out.println("Exception while running tests\n" + e);
        }
        if (exitcode == 0) {
            // DEBUG
            System.out.println("Tests passed");
            return true;
        } else {
            System.out.println("Tests did not pass");
            return false;
        }
    }

}
