package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.Unit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO {

    public List<Unit> getAllUnits() throws SQLException {
        List<Unit> units = new ArrayList<>();

        String sql = """
                SELECT
                    u.UnitID,
                    u.UnitTitle,
                    u.EstimatedCompletionTime,
                    u.SequenceOrder,
                    u.ProgramID,
                    p.ProgramTitle
                FROM [Unit] u
                JOIN [Program] p ON u.ProgramID = p.ProgramID
                ORDER BY p.ProgramTitle, u.SequenceOrder
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                units.add(mapResultSetToUnit(resultSet));
            }
        }

        return units;
    }

    public void addUnit(String unitTitle,
                        int estimatedCompletionTime,
                        int sequenceOrder,
                        int programID) throws SQLException {
        String sql = """
                INSERT INTO [Unit] (UnitTitle, EstimatedCompletionTime, SequenceOrder, ProgramID)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, unitTitle);
            statement.setInt(2, estimatedCompletionTime);
            statement.setInt(3, sequenceOrder);
            statement.setInt(4, programID);

            statement.executeUpdate();
        }
    }

    public void updateUnit(int unitID,
                           String unitTitle,
                           int estimatedCompletionTime,
                           int sequenceOrder,
                           int programID) throws SQLException {
        String sql = """
                UPDATE [Unit]
                SET UnitTitle = ?,
                    EstimatedCompletionTime = ?,
                    SequenceOrder = ?,
                    ProgramID = ?
                WHERE UnitID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, unitTitle);
            statement.setInt(2, estimatedCompletionTime);
            statement.setInt(3, sequenceOrder);
            statement.setInt(4, programID);
            statement.setInt(5, unitID);

            statement.executeUpdate();
        }
    }

    public void deleteUnit(int unitID) throws SQLException {
        String sql = """
                DELETE FROM [Unit]
                WHERE UnitID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, unitID);
            statement.executeUpdate();
        }
    }

    private Unit mapResultSetToUnit(ResultSet resultSet) throws SQLException {
        return new Unit(
                resultSet.getInt("UnitID"),
                resultSet.getString("UnitTitle"),
                resultSet.getInt("EstimatedCompletionTime"),
                resultSet.getInt("SequenceOrder"),
                resultSet.getInt("ProgramID"),
                resultSet.getString("ProgramTitle")
        );
    }
}