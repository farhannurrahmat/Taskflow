package com.taskflow.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private static final String DB_URL = "jdbc:sqlite:taskflow.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initialize() {
        try {
            Connection conn = getConnection();
            createTables(conn);
            seedData(conn);
            System.out.println("[DB] Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[DB] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Users table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                full_name TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'member'
            )
        """);

        // Tasks table (group/team tasks)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                project_name TEXT NOT NULL,
                assigned_to INTEGER,
                priority TEXT NOT NULL DEFAULT 'Medium',
                status TEXT NOT NULL DEFAULT 'To Do',
                deadline TEXT NOT NULL,
                completed_at TEXT,
                created_at TEXT NOT NULL DEFAULT (datetime('now','localtime')),
                FOREIGN KEY (assigned_to) REFERENCES users(id)
            )
        """);

        // Personal tasks table (tugas mandiri)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS personal_tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                category TEXT NOT NULL DEFAULT 'Kuliah',
                user_id INTEGER NOT NULL,
                priority TEXT NOT NULL DEFAULT 'Medium',
                status TEXT NOT NULL DEFAULT 'To Do',
                deadline TEXT NOT NULL,
                completed_at TEXT,
                created_at TEXT NOT NULL DEFAULT (datetime('now','localtime')),
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """);

        stmt.close();
    }

    private static void seedData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Check if users already exist
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) > 0) {
            rs.close();
            stmt.close();
            return;
        }
        rs.close();

        // Insert default manager only - users must register themselves
        stmt.execute("""
            INSERT INTO users (username, password, full_name, role) VALUES
            ('manager', 'manager123', 'Big Bos', 'manager')
        """);

        stmt.close();
    }
}
