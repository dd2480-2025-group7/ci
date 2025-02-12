/**
 * This package contains the core functionality for the CI application.
 * <p>
 * The server checks for commits in the repo, and automatically uses Maven for
 * building and running the tests on that commit. If everything passes, the
 * server sends a post request for setting a checkmark on the commit, to mark it
 * as successful. Otherwise, it sets a red cross on the commit to mark it as
 * unsuccessful. GitHub actions are enabled such that on each commit, there are
 * two checkmarks which are synchronized. The server also has tests for checking
 * that everything is working as expected. Additionally, there is a GitHub
 * Actions workflow for building and publishing the javadoc API documentation to
 * GitHub pages.
 * </p>
 */
package com.group7.ciapp;
