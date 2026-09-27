package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.Instructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    public List<Instructor> getAllInstructors() throws SQLException {
        List<Instructor> instructors = new ArrayList<>();

        String sql = """
                SELECT InstructorID, FirstName, LastName, Email, Expertise
                FROM Instructor
                ORDER BY InstructorID
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Instructor instructor = new Instructor(
                        resultSet.getInt("InstructorID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("Email"),
                        resultSet.getString("Expertise")
                );

                instructors.add(instructor);
            }
        }

        return instructors;
    }

    public void addInstructor(String firstName, String lastName,
                              String email, String expertise) throws SQLException {
        String sql = """
                INSERT INTO Instructor (FirstName, LastName, Email, Expertise)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, expertise);

            statement.executeUpdate();
        }
    }

    public void updateInstructor(int instructorID, String firstName, String lastName,
                                 String email, String expertise) throws SQLException {
        String sql = """
                UPDATE Instructor
                SET FirstName = ?, LastName = ?, Email = ?, Expertise = ?
                WHERE InstructorID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, expertise);
            statement.setInt(5, instructorID);

            statement.executeUpdate();
        }
    }

    public void deleteInstructor(int instructorID) throws SQLException {
        String sql = "DELETE FROM Instructor WHERE InstructorID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, instructorID);
            statement.executeUpdate();
        }
    }
}