package com.academicportal.ui;

import com.academicportal.dao.CategoryDAO;
import com.academicportal.model.Category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CategoriesView extends VBox {

    private final TextField categoryIDField = new TextField();
    private final TextField categoryNameField = new TextField();
    private final TextArea descriptionArea = new TextArea();

    private final TableView<Category> tableView = new TableView<>();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final Label statusLabel = new Label();

    public CategoriesView() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Categories Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        categoryIDField.setPromptText("Category ID");
        categoryIDField.setEditable(false);

        categoryNameField.setPromptText("Category Name");

        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        GridPane form = createForm();

        Button addButton = new Button("Add Category");
        Button updateButton = new Button("Update Category");
        Button deleteButton = new Button("Delete Category");
        Button loadButton = new Button("Load Categories");
        Button clearButton = new Button("Clear");

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, loadButton, clearButton);

        statusLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        createTable();

        addButton.setOnAction(event -> addCategory());
        updateButton.setOnAction(event -> updateCategory());
        deleteButton.setOnAction(event -> deleteCategory());
        loadButton.setOnAction(event -> loadCategories());
        clearButton.setOnAction(event -> clearForm());

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedCategory) -> {
                    if (selectedCategory != null) {
                        fillForm(selectedCategory);
                    }
                }
        );

        getChildren().addAll(title, form, buttons, statusLabel, tableView);

        loadCategories();
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Category ID:"), 0, 0);
        form.add(categoryIDField, 1, 0);

        form.add(new Label("Category Name:"), 0, 1);
        form.add(categoryNameField, 1, 1);

        form.add(new Label("Description:"), 0, 2);
        form.add(descriptionArea, 1, 2);

        return form;
    }

    private void createTable() {
        TableColumn<Category, Integer> idColumn = new TableColumn<>("CategoryID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("categoryID"));

        TableColumn<Category, String> nameColumn = new TableColumn<>("CategoryName");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<Category, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(
                idColumn,
                nameColumn,
                descriptionColumn
        );

        tableView.setItems(categories);
        tableView.setPrefHeight(350);
    }

    private void loadCategories() {
        try {
            categories.setAll(categoryDAO.getAllCategories());
            tableView.refresh();
            statusLabel.setText("Loaded " + categories.size() + " categories successfully.");
        } catch (Exception e) {
            showError("Load Categories Error", e.getMessage());
        }
    }

    private void addCategory() {
        try {
            if (categoryNameField.getText().isBlank()) {
                showError("Validation Error", "Category Name is required.");
                return;
            }

            categoryDAO.addCategory(
                    categoryNameField.getText(),
                    descriptionArea.getText()
            );

            loadCategories();
            clearForm();
            showInfo("Success", "Category added successfully.");
            statusLabel.setText("Category added and table reloaded.");

        } catch (Exception e) {
            showError("Add Category Error", e.getMessage());
        }
    }

    private void updateCategory() {
        try {
            if (categoryIDField.getText().isBlank()) {
                showError("Selection Error", "Select a category from the table first.");
                return;
            }

            if (categoryNameField.getText().isBlank()) {
                showError("Validation Error", "Category Name is required.");
                return;
            }

            int categoryID = Integer.parseInt(categoryIDField.getText());

            categoryDAO.updateCategory(
                    categoryID,
                    categoryNameField.getText(),
                    descriptionArea.getText()
            );

            loadCategories();
            clearForm();
            showInfo("Success", "Category updated successfully.");
            statusLabel.setText("Category updated and table reloaded.");

        } catch (Exception e) {
            showError("Update Category Error", e.getMessage());
        }
    }

    private void deleteCategory() {
        try {
            if (categoryIDField.getText().isBlank()) {
                showError("Selection Error", "Select a category from the table first.");
                return;
            }

            int categoryID = Integer.parseInt(categoryIDField.getText());

            categoryDAO.deleteCategory(categoryID);

            loadCategories();
            clearForm();
            showInfo("Success", "Category deleted successfully.");
            statusLabel.setText("Category deleted and table reloaded.");

        } catch (Exception e) {
            showError("Delete Category Error", e.getMessage());
        }
    }

    private void fillForm(Category category) {
        categoryIDField.setText(String.valueOf(category.getCategoryID()));
        categoryNameField.setText(category.getCategoryName());
        descriptionArea.setText(category.getDescription());
    }

    private void clearForm() {
        categoryIDField.clear();
        categoryNameField.clear();
        descriptionArea.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}