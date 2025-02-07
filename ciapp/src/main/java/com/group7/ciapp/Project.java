package com.group7.ciapp;

import org.eclipse.jgit.api.Git;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Project {
    private String url;
    private String commitHash;
    private Git git;
    private int checkId;

    public Project(String url, String commitHash, int checkId) {
        this.url = url;
        this.commitHash = commitHash;
        this.checkId = checkId;
    }

    // create main method that uses JGit to clone the repository
    public static void main(String url, String commitHash) {
    }

    public String cloneRepo(){
        System.out.println("Repo URL: " + url);
        System.out.println("Commit: " + commitHash);
        // Clone repo inside tmp directory
        String path = System.getProperty("java.io.tmpdir") + "/ci-tests/" + this.checkId;
        System.out.println("Cloning repository to: " + path);

        try {
            this.git = Git.cloneRepository().setURI(url).setDirectory(new File(path)).call();
            // checkout specific commit
            this.git.checkout().setName(commitHash).call();
            this.git.close();
            return path;

        } catch (Exception e) {
            System.out.println("Error cloning repository");
            // print exception code
            System.out.println(e);
            return null;
        }
    }

    public void deleteRepo(String path){
        // Delete the cloned repository efter running tests
        this.git.close();
        this.git = null;
        Git.shutdown();
        File dir = new File(path);
        // TODO: Try this while testing dir.mkdirs(); // Ensure the directory exists
        removeRecursively(dir);
    }

     /**
     * Recursively remove a file or directory from the given path
     * 
     * @param f (File) The file or directory to remove
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
     * Runs Maven tests. If all tests pass, return true, else, return false.
     * 
     * @param path (String) The path to where the repo is cloned
     */
    public boolean runMavenTests(String path) {
        int exitcode = -1;

        try {
            ProcessBuilder builder = new ProcessBuilder("mvn", "package");
            builder.directory(new File(path + "/ciapp"));
            builder.redirectErrorStream(true); // merges stdout and stderr
            Process process = builder.start();

            // Log from process
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = in.readLine()) != null){
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
