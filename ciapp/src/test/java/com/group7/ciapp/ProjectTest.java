package com.group7.ciapp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProjectTest {

    private static Project project;
    private static final String testUrl = "https://github.com/dd2480-2025-group7/ci";
    private static final String testCommitHash = "f8fffc3f13557144a1c2563f7628139031dbe7b1";
    private static final Long testCheckId = 1L;

    @BeforeAll
    static void setUp() {
        project = new Project(testUrl, testCommitHash, testCheckId);
    }

    /**
     * Clone the repo and check if the path is correct. The path should not be null
     * and the path should exist.
     */
    @Test
    void testCloneRepo_true() {
        String path = project.cloneRepo();

        boolean pathExists = path.contains("ci-tests/" + testCheckId);
        project.deleteRepo(path); // delete repo so we don't have to do it manually
        assertNotNull(path);
        assertTrue(pathExists);
    }

    /**
     * Clone the repo and compare it with an incorrect path. The path should not be
     * null since it is a valid path.
     */
    @Test
    void testCloneRepo_false() {
        String path = project.cloneRepo();

        boolean pathEquality = path.contains("ci-tests/" + 2);
        project.deleteRepo(path); // delete repo so we don't have to do it manually
        assertNotNull(path);
        assertFalse(pathEquality);
    }

    /**
     * Tries to clone repo twice, should be catched as an error and return null
     * since previous repo was not deleted.
     */
    @Test
    void testCloneRepo_duplicate() {
        String path = project.cloneRepo();
        String path2 = project.cloneRepo();

        project.deleteRepo(path); // delete repo so we don't have to do it manually
        assertNotNull(path);
        assertNull(path2);

    }
}
