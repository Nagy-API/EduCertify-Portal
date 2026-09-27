package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.Credential;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CredentialDAO {

    public List<Credential> getAllCredentials() throws SQLException {
        String sql = """
                SELECT c.CredentialID, c.VerificationCode, c.GrantedDate,
                       c.StudentID, c.ProgramID,
                       CONCAT(s.FirstName, ' ', s.LastName) AS StudentName,
                       p.ProgramTitle
                FROM Credential c
                JOIN Student s ON c.StudentID = s.StudentID
                JOIN [Program] p ON c.ProgramID = p.ProgramID
                ORDER BY c.CredentialID
                """;

        return runCredentialQuery(sql, null);
    }

    public List<Credential> verifyCredential(String code) throws SQLException {
        String sql = """
                SELECT c.CredentialID, c.VerificationCode, c.GrantedDate,
                       c.StudentID, c.ProgramID,
                       CONCAT(s.FirstName, ' ', s.LastName) AS StudentName,
                       p.ProgramTitle
                FROM Credential c
                JOIN Student s ON c.StudentID = s.StudentID
                JOIN [Program] p ON c.ProgramID = p.ProgramID
                WHERE c.VerificationCode = ?
                """;

        return runCredentialQuery(sql, code);
    }

    public void issueCredential(int studentID, int programID, String code) throws SQLException {
        String sql = """
                INSERT INTO Credential (VerificationCode, StudentID, ProgramID)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code);
            statement.setInt(2, studentID);
            statement.setInt(3, programID);
            statement.executeUpdate();
        }
    }

    private List<Credential> runCredentialQuery(String sql, String code) throws SQLException {
        List<Credential> credentials = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (code != null) {
                statement.setString(1, code);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp timestamp = resultSet.getTimestamp("GrantedDate");

                    credentials.add(new Credential(
                            resultSet.getInt("CredentialID"),
                            resultSet.getString("VerificationCode"),
                            timestamp == null ? null : timestamp.toLocalDateTime(),
                            resultSet.getInt("StudentID"),
                            resultSet.getInt("ProgramID"),
                            resultSet.getString("StudentName"),
                            resultSet.getString("ProgramTitle")
                    ));
                }
            }
        }

        return credentials;
    }
}