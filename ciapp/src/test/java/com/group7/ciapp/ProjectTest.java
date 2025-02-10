package com.group7.ciapp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

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
        File dir = new File(path);
        boolean pathExists = dir.exists();
        project.deleteRepo(path); // delete repo so we don't have to do it manually
        assertNotNull(path);
        assertTrue(pathExists);
    }

    /**
     * Clone the repo and compare it with an non-existing path. The original path should not be
     * null since it is a valid path. Path2 should not exist.
     */
    @Test
    void testCloneRepo_false() {
        Project project2 = new Project(ProjectTest.testUrl, ProjectTest.testCommitHash, 2L);
        String path = project2.cloneRepo();
        String path2 = path.substring(0, path.length() - 1);
        path2 = path2 + "1";

        File dir = new File(path2);
        boolean pathExists = dir.exists();
        project.deleteRepo(path); // delete repo so we don't have to do it manually
        assertNotNull(path);
        assertFalse(pathExists);
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
