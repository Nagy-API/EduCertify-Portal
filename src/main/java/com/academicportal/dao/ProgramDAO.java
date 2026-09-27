package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.Program;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramDAO {

    public List<Program> getAllPrograms() throws SQLException {
        List<Program> programs = new ArrayList<>();

        String sql = """
                SELECT
                    p.ProgramID,
                    p.ProgramTitle,
                    p.DifficultyLevel,
                    p.RegistrationFee,
                    p.[Status],
                    p.InstructorID,
                    p.CategoryID,
                    CONCAT(i.FirstName, ' ', i.LastName) AS InstructorName,
                    c.CategoryName
                FROM [Program] p
                JOIN Instructor i ON p.InstructorID = i.InstructorID
                JOIN Category c ON p.CategoryID = c.CategoryID
                ORDER BY p.ProgramID
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                programs.add(mapResultSetToProgram(resultSet));
            }
        }

        return programs;
    }

    public List<Program> filterPrograms(Integer categoryID,
                                        String difficultyLevel,
                                        Double minPrice,
                                        Double maxPrice) throws SQLException {
        List<Program> programs = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.ProgramID,
                    p.ProgramTitle,
                    p.DifficultyLevel,
                    p.RegistrationFee,
                    p.[Status],
                    p.InstructorID,
                    p.CategoryID,
                    CONCAT(i.FirstName, ' ', i.LastName) AS InstructorName,
                    c.CategoryName
                FROM [Program] p
                JOIN Instructor i ON p.InstructorID = i.InstructorID
                JOIN Category c ON p.CategoryID = c.CategoryID
                WHERE 1 = 1
                """);

        List<Object> parameters = new ArrayList<>();

        if (categoryID != null) {
            sql.append(" AND p.CategoryID = ? ");
            parameters.add(categoryID);
        }

        if (difficultyLevel != null && !difficultyLevel.isBlank()) {
            sql.append(" AND p.DifficultyLevel = ? ");
            parameters.add(difficultyLevel);
        }

        if (minPrice != null) {
            sql.append(" AND p.RegistrationFee >= ? ");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND p.RegistrationFee <= ? ");
            parameters.add(maxPrice);
        }

        sql.append(" ORDER BY p.ProgramID ");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    programs.add(mapResultSetToProgram(resultSet));
                }
            }
        }

        return programs;
    }

    public void addProgram(String programTitle,
                           String difficultyLevel,
                           double registrationFee,
                           String status,
                           int instructorID,
                           int categoryID) throws SQLException {
        String sql = """
                INSERT INTO [Program]
                (ProgramTitle, DifficultyLevel, RegistrationFee, [Status], InstructorID, CategoryID)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, programTitle);
            statement.setString(2, difficultyLevel);
            statement.setDouble(3, registrationFee);
            statement.setString(4, status);
            statement.setInt(5, instructorID);
            statement.setInt(6, categoryID);

            statement.executeUpdate();
        }
    }

    public void updateProgram(int programID,
                              String programTitle,
                              String difficultyLevel,
                              double registrationFee,
                              String status,
                              int instructorID,
                              int categoryID) throws SQLException {
        String sql = """
                UPDATE [Program]
                SET ProgramTitle = ?,
                    DifficultyLevel = ?,
                    RegistrationFee = ?,
                    [Status] = ?,
                    InstructorID = ?,
                    CategoryID = ?
                WHERE ProgramID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, programTitle);
            statement.setString(2, difficultyLevel);
            statement.setDouble(3, registrationFee);
            statement.setString(4, status);
            statement.setInt(5, instructorID);
            statement.setInt(6, categoryID);
            statement.setInt(7, programID);

            statement.executeUpdate();
        }
    }

    public void deleteProgram(int programID) throws SQLException {
        String sql = """
                DELETE FROM [Program]
                WHERE ProgramID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, programID);
            statement.executeUpdate();
        }
    }

    private Program mapResultSetToProgram(ResultSet resultSet) throws SQLException {
        return new Program(
                resultSet.getInt("ProgramID"),
                resultSet.getString("ProgramTitle"),
                resultSet.getString("DifficultyLevel"),
                resultSet.getDouble("RegistrationFee"),
                resultSet.getString("Status"),
                resultSet.getInt("InstructorID"),
                resultSet.getInt("CategoryID"),
                resultSet.getString("InstructorName"),
                resultSet.getString("CategoryName")
        );
    }
}