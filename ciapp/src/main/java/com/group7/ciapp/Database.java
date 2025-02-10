package com.group7.ciapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    private static String Url;

    /**
     * Constructor for Database
     * 
     * @param Path
     */
    public Database(String Path) {
        try {
            Class.forName("org.sqlite.JDBC"); // Load SQLite driver explicitly
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Url = "jdbc:sqlite:" + Path; // Ensure static variable is initialized
    }

    /**
     * Create the build table if it does not exist
     * 
     * @return void
     */
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS build ("
                + "id INTEGER PRIMARY KEY, "
                + "commit_hash TEXT NOT NULL, "
                + "build_date DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "build_log TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(this.Url);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a build into the build table
     * 
     * @param id
     * @param commitHash
     * @param logOutput
     */
    public static void insertBuild(Long id, String commitHash, String logOutput) {
        String sql = "INSERT INTO build(id, commit_hash, build_log) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(Url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.setString(2, commitHash);
            pstmt.setString(3, logOutput);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all builds from the build table
     * 
     * @return ArrayList<Build>
     */
    public static ArrayList<Build> getBuilds() {
        String sql = "SELECT * FROM build";
        ArrayList<Build> builds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Url);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                builds.add(new Build(rs.getLong("id"), rs.getString("commit_hash"), rs.getString("build_date"),
                        rs.getString("build_log")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return builds;
    }

    /**
     * Get a build from the build table
     * 
     * @param id
     * @return Build
     */
    public static Build getBuild(Long id) {
        String sql = "SELECT * FROM build WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(Url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Build(rs.getLong("id"), rs.getString("commit_hash"), rs.getString("build_date"),
                            rs.getString("build_log"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
