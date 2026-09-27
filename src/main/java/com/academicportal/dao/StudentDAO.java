package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();

        String sql = """
                SELECT StudentID, FirstName, LastName, Email, Phone, RegistrationDate
                FROM Student
                ORDER BY StudentID
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Timestamp registrationTimestamp = resultSet.getTimestamp("RegistrationDate");

                Student student = new Student(
                        resultSet.getInt("StudentID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("Email"),
                        resultSet.getString("Phone"),
                        registrationTimestamp == null ? null : registrationTimestamp.toLocalDateTime()
                );

                students.add(student);
            }
        }

        return students;
    }

    public void addStudent(String firstName, String lastName, String email, String phone) throws SQLException {
        String sql = """
                INSERT INTO Student (FirstName, LastName, Email, Phone)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, phone);

            statement.executeUpdate();
        }
    }

    public void updateStudent(int studentID, String firstName, String lastName,
                              String email, String phone) throws SQLException {
        String sql = """
                UPDATE Student
                SET FirstName = ?, LastName = ?, Email = ?, Phone = ?
                WHERE StudentID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setInt(5, studentID);

            statement.executeUpdate();
        }
    }

    public void deleteStudent(int studentID) throws SQLException {
        String sql = "DELETE FROM Student WHERE StudentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentID);
            statement.executeUpdate();
        }
    }
}
