package com.academicportal.dao;

import com.academicportal.db.DatabaseConnection;
import com.academicportal.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();

        String sql = """
                SELECT CategoryID, CategoryName, Description
                FROM Category
                ORDER BY CategoryID
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Category category = new Category(
                        resultSet.getInt("CategoryID"),
                        resultSet.getString("CategoryName"),
                        resultSet.getString("Description")
                );

                categories.add(category);
            }
        }

        return categories;
    }

    public void addCategory(String categoryName, String description) throws SQLException {
        String sql = """
                INSERT INTO Category (CategoryName, Description)
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoryName);
            statement.setString(2, description);

            statement.executeUpdate();
        }
    }

    public void updateCategory(int categoryID, String categoryName, String description) throws SQLException {
        String sql = """
                UPDATE Category
                SET CategoryName = ?, Description = ?
                WHERE CategoryID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoryName);
            statement.setString(2, description);
            statement.setInt(3, categoryID);

            statement.executeUpdate();
        }
    }

    public void deleteCategory(int categoryID) throws SQLException {
        String sql = """
                DELETE FROM Category
                WHERE CategoryID = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoryID);

            statement.executeUpdate();
        }
    }
}