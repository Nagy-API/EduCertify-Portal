package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();

        String sql = """
                SELECT
                    e.StudentID,
                    e.ProgramID,
                    e.SignupDate,
                    e.ProgressPercentage,
                    e.CompletionStatus,
                    CONCAT(s.FirstName, ' ', s.LastName) AS StudentName,
                    p.ProgramTitle
                FROM Enrollment e
                JOIN Student s ON e.StudentID = s.StudentID
                JOIN [Program] p ON e.ProgramID = p.ProgramID
                ORDER BY e.SignupDate DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                enrollments.add(mapResultSetToEnrollment(resultSet));
            }
        }

        return enrollments;
    }

    public void addEnrollment(int studentID,
                              int programID,
                              int progressPercentage,
                              String completionStatus) throws SQLException {
        String sql = """
                INSERT INTO Enrollment
                (StudentID, ProgramID, ProgressPercentage, CompletionStatus)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentID);
            statement.setInt(2, programID);
            statement.setInt(3, progressPercentage);
            statement.setString(4, completionStatus);

            statement.executeUpdate();
        }
    }

    public void updateProgressPercentage(int studentID,
                                         int programID,
                                         int progressPercentage) throws SQLException {
        String sql = """
                UPDATE Enrollment
                SET ProgressPercentage = ?
                WHERE StudentID = ? AND ProgramID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, progressPercentage);
            statement.setInt(2, studentID);
            statement.setInt(3, programID);

            statement.executeUpdate();
        }
    }

    public void updateCompletionStatus(int studentID,
                                       int programID,
                                       String completionStatus) throws SQLException {
        String sql = """
                UPDATE Enrollment
                SET CompletionStatus = ?
                WHERE StudentID = ? AND ProgramID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, completionStatus);
            statement.setInt(2, studentID);
            statement.setInt(3, programID);

            statement.executeUpdate();
        }
    }

    private Enrollment mapResultSetToEnrollment(ResultSet resultSet) throws SQLException {
        Timestamp signupTimestamp = resultSet.getTimestamp("SignupDate");

        return new Enrollment(
                resultSet.getInt("StudentID"),
                resultSet.getInt("ProgramID"),
                signupTimestamp == null ? null : signupTimestamp.toLocalDateTime(),
                resultSet.getInt("ProgressPercentage"),
                resultSet.getString("CompletionStatus"),
                resultSet.getString("StudentName"),
                resultSet.getString("ProgramTitle")
        );
    }
}