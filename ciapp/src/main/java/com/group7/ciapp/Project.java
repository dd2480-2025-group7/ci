package com.group7.ciapp;

import org.eclipse.jgit.api.Git;
import java.io.File;

public class Project {
    private String url;
    private String commit;
    private Git git;

    public Project(String url, String commit) {
        this.url = url;
        this.commit = commit;
    }

    // create main method that uses JGit to clone the repository
    public static void main(String url, String commit) {
    }

    public boolean start() {
        // create a new Git object
        // git = Git.init().setDirectory("/path/to/repo").call();

        System.out.println("Repo URL: " + url);
        System.out.println("Commit: " + commit);

        String path = System.getProperty("user.home") + "/cirepo"; // clone repo inside users home directory

        System.out.println("Cloning repository to: " + path);
        try {
            Git git = Git.cloneRepository().setURI(url).setDirectory(new File(path)).call();
            // checkout specific commit
            // "git checkout < commit >"
            git.checkout().setName(commit).call();

            git.close();
        } catch (Exception e) {
            System.out.println("Error cloning repository");
            // print exception code
            System.out.println(e);
            return false;
        }

        // run tests for specified project after cloning
        // return boolean based on test results
        boolean result = runMavenTests(path);

        // delete the cloned repository, forecfully the whole directory using java

        // if tests pass, return true
        // if tests fail, return false
        return result;
    }

    private boolean runMavenTests(String path) {
        int exitcode = -1;
        try {
            ProcessBuilder builder = new ProcessBuilder("mvn", "package");
            builder.directory(new File(path + "/ciapp"));
            builder.redirectErrorStream(true); // merges stdout and stderr
            Process process = builder.start();

            exitcode = process.waitFor(); // wait for process to finish running tests

        } catch (Exception e) {

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

    // create method here for running tests using maven
    // run tests for specified project after cloning
}
