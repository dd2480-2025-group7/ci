package com.group7.ciapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProjectTest {

    private static Project project;
    private static final String testUrl = "https://github.com/dd2480-2025-group7/ci";
    private static final String testCommitHash = "f8fffc3f13557144a1c2563f7628139031dbe7b1";
    private static final Long testCheckId = 1L;
    private static ProcessExecutor mockExecutor;

    @BeforeAll
    static void setUp() {
        mockExecutor = mock(ProcessExecutor.class);
        project = new Project(testUrl, testCommitHash, testCheckId, mockExecutor);
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
     * Clone the repo and compare it with an non-existing path. The original path
     * should not be null since it is a valid path. Path2 should not exist.
     */
    @Test
    void testCloneRepo_false() {
        String path = project.cloneRepo();
        String path2 = path.substring(0, path.length() - 1);
        path2 = path2 + "2";

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

    /**
     * Checks that repository deletion works. The directory should not exist after
     * deletion.
     */
    @Test
    void testDeleteRepo_true() {
        String path = project.cloneRepo();
        File dir = new File(path);
        project.deleteRepo(path); // delete repo so we don't have to do it manually
        boolean pathDoesNotExists = !dir.exists();
        assertTrue(pathDoesNotExists);
    }

    /**
     * Checks if deletion was unsuccesful when wrong input path was given
     */
    @Test
    void testDeleteRepo_false() {
        // Clone the repo
        String path = project.cloneRepo();

        // Create another path variable that does not have an actual repo
        String path2 = path.substring(0, path.length() - 1);
        path2 += "2";

        // Delete the non-existing repo
        project.deleteRepo(path2);

        // Check if the original repo still exists (which it should)
        File dir = new File(path);
        boolean pathExists = dir.exists();
        assertTrue(pathExists);

        // Delete the existing repo so we don't have to do it manually
        project.deleteRepo(path);
    }

     /**
     * Test if the runMavenTests return true when the tests pass.
     * 
     * @throws Exception
     */
    @Test
    void runMavenTestsSuccess() throws Exception {
        // clone repo
        String path = project.cloneRepo();

        // mock process
        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream("Build Success".getBytes()));
        when(mockProcess.waitFor()).thenReturn(0);
        when(mockExecutor.startProcess(any(File.class))).thenReturn(mockProcess);

        boolean result = project.runMavenTests(path);
        assertTrue(result, "Maven tests should pass (exit code 0)");

        // delete repo
        project.deleteRepo(path);
    }

    /**
     * Test if the runMavenTests return true when the tests pass.
     * 
     * @throws Exception
     */
    @Test
    void runMavenTestsFailure() throws Exception {
        // clone repo
        String path = project.cloneRepo();

        // mock process
        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream("Build Not success".getBytes()));
        when(mockProcess.waitFor()).thenReturn(1);
        when(mockExecutor.startProcess(any(File.class))).thenReturn(mockProcess);

        boolean result = project.runMavenTests(path);
        assertFalse(result, "Maven tests should fail (exit code != 0)");

        // delete repo
        project.deleteRepo(path);
    }
}