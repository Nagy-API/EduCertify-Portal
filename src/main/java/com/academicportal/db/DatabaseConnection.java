package com.academicportal.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=AcademicPortal;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=5;";

    public static Connection getConnection() throws SQLException {
        String user = System.getenv("EDUCERTIFY_DB_USER");
        String password = System.getenv("EDUCERTIFY_DB_PASSWORD");
        if (user == null || user.isBlank() || password == null || password.isBlank()) {
            throw new SQLException("Set EDUCERTIFY_DB_USER and EDUCERTIFY_DB_PASSWORD before connecting to SQL Server.");
        }

        String configuredUrl = System.getenv("EDUCERTIFY_DB_URL");
        String url = configuredUrl == null || configuredUrl.isBlank() ? DEFAULT_URL : configuredUrl;
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return false;
        }
    }
}
