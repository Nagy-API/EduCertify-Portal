package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.UnitProgress;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnitProgressDAO {

    public List<UnitProgress> getAllUnitProgress() throws SQLException {
        List<UnitProgress> progressList = new ArrayList<>();

        String sql = """
                SELECT
                    up.StudentID,
                    up.UnitID,
                    up.UnitStatus,
                    up.CompletionDate,
                    CONCAT(s.FirstName, ' ', s.LastName) AS StudentName,
                    u.UnitTitle,
                    p.ProgramTitle
                FROM UnitProgress up
                JOIN Student s ON up.StudentID = s.StudentID
                JOIN [Unit] u ON up.UnitID = u.UnitID
                JOIN [Program] p ON u.ProgramID = p.ProgramID
                ORDER BY s.StudentID, p.ProgramTitle, u.SequenceOrder
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                progressList.add(mapResultSetToUnitProgress(resultSet));
            }
        }

        return progressList;
    }

    public void markUnitAsCompleted(int studentID, int unitID) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (unitProgressExists(connection, studentID, unitID)) {
                String sql = """
                        UPDATE UnitProgress
                        SET UnitStatus = 'Completed',
                            CompletionDate = GETDATE()
                        WHERE StudentID = ? AND UnitID = ?
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, studentID);
                    statement.setInt(2, unitID);
                    statement.executeUpdate();
                }

            } else {
                String sql = """
                        INSERT INTO UnitProgress (StudentID, UnitID, UnitStatus, CompletionDate)
                        VALUES (?, ?, 'Completed', GETDATE())
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, studentID);
                    statement.setInt(2, unitID);
                    statement.executeUpdate();
                }
            }
        }
    }

    public void updateUnitStatus(int studentID, int unitID, String unitStatus) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (unitProgressExists(connection, studentID, unitID)) {
                String sql;

                if ("Completed".equals(unitStatus)) {
                    sql = """
                            UPDATE UnitProgress
                            SET UnitStatus = ?,
                                CompletionDate = ISNULL(CompletionDate, GETDATE())
                            WHERE StudentID = ? AND UnitID = ?
                            """;
                } else {
                    sql = """
                            UPDATE UnitProgress
                            SET UnitStatus = ?,
                                CompletionDate = NULL
                            WHERE StudentID = ? AND UnitID = ?
                            """;
                }

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, unitStatus);
                    statement.setInt(2, studentID);
                    statement.setInt(3, unitID);
                    statement.executeUpdate();
                }

            } else {
                String sql;

                if ("Completed".equals(unitStatus)) {
                    sql = """
                            INSERT INTO UnitProgress (StudentID, UnitID, UnitStatus, CompletionDate)
                            VALUES (?, ?, ?, GETDATE())
                            """;
                } else {
                    sql = """
                            INSERT INTO UnitProgress (StudentID, UnitID, UnitStatus, CompletionDate)
                            VALUES (?, ?, ?, NULL)
                            """;
                }

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, studentID);
                    statement.setInt(2, unitID);
                    statement.setString(3, unitStatus);
                    statement.executeUpdate();
                }
            }
        }
    }

    private boolean unitProgressExists(Connection connection, int studentID, int unitID) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS ProgressCount
                FROM UnitProgress
                WHERE StudentID = ? AND UnitID = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentID);
            statement.setInt(2, unitID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("ProgressCount") > 0;
                }
            }
        }

        return false;
    }

    private UnitProgress mapResultSetToUnitProgress(ResultSet resultSet) throws SQLException {
        Timestamp completionTimestamp = resultSet.getTimestamp("CompletionDate");

        return new UnitProgress(
                resultSet.getInt("StudentID"),
                resultSet.getInt("UnitID"),
                resultSet.getString("UnitStatus"),
                completionTimestamp == null ? null : completionTimestamp.toLocalDateTime(),
                resultSet.getString("StudentName"),
                resultSet.getString("UnitTitle"),
                resultSet.getString("ProgramTitle")
        );
    }
}