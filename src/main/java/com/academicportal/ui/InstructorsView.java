package com.academicportal.ui;

import com.academicportal.dao.InstructorDAO;
import com.academicportal.model.Instructor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InstructorsView extends VBox {

    private final TextField instructorIDField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField expertiseField = new TextField();

    private final TableView<Instructor> tableView = new TableView<>();
    private final ObservableList<Instructor> instructors = FXCollections.observableArrayList();

    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final Label statusLabel = new Label();
    public InstructorsView() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Instructors Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        instructorIDField.setPromptText("Instructor ID");
        instructorIDField.setEditable(false);

        firstNameField.setPromptText("First Name");
        lastNameField.setPromptText("Last Name");
        emailField.setPromptText("Email");
        expertiseField.setPromptText("Expertise");

        GridPane form = createForm();

        Button addButton = new Button("Add Instructor");
        Button updateButton = new Button("Update Instructor");
        Button deleteButton = new Button("Delete Instructor");
        Button loadButton = new Button("Load Instructors");
        Button clearButton = new Button("Clear");

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, loadButton, clearButton);
        statusLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        createTable();

        addButton.setOnAction(event -> addInstructor());
        updateButton.setOnAction(event -> updateInstructor());
        deleteButton.setOnAction(event -> deleteInstructor());
        loadButton.setOnAction(event -> loadInstructors());
        clearButton.setOnAction(event -> clearForm());

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedInstructor) -> {
                    if (selectedInstructor != null) {
                        fillForm(selectedInstructor);
                    }
                }
        );

        getChildren().addAll(title, form, buttons, statusLabel, tableView);
        loadInstructors();
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Instructor ID:"), 0, 0);
        form.add(instructorIDField, 1, 0);

        form.add(new Label("First Name:"), 0, 1);
        form.add(firstNameField, 1, 1);

        form.add(new Label("Last Name:"), 0, 2);
        form.add(lastNameField, 1, 2);

        form.add(new Label("Email:"), 0, 3);
        form.add(emailField, 1, 3);

        form.add(new Label("Expertise:"), 0, 4);
        form.add(expertiseField, 1, 4);

        return form;
    }

    private void createTable() {
        TableColumn<Instructor, Integer> idColumn = new TableColumn<>("InstructorID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("instructorID"));

        TableColumn<Instructor, String> firstNameColumn = new TableColumn<>("FirstName");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Instructor, String> lastNameColumn = new TableColumn<>("LastName");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Instructor, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Instructor, String> expertiseColumn = new TableColumn<>("Expertise");
        expertiseColumn.setCellValueFactory(new PropertyValueFactory<>("expertise"));

        tableView.getColumns().addAll(
                idColumn,
                firstNameColumn,
                lastNameColumn,
                emailColumn,
                expertiseColumn
        );

        tableView.setItems(instructors);
        tableView.setPrefHeight(300);
    }

    private void loadInstructors() {
        try {
            instructors.setAll(instructorDAO.getAllInstructors());
            tableView.refresh();
            statusLabel.setText("Loaded " + instructors.size() + " instructors successfully.");
        } catch (Exception e) {
            showError("Load Instructors Error", e.getMessage());
        }
    }

    private void addInstructor() {
        try {
            if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()) {
                showError("Validation Error", "First Name and Last Name are required.");
                return;
            }

            instructorDAO.addInstructor(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    expertiseField.getText()
            );

            loadInstructors();
            clearForm();
            showInfo("Success", "Instructor added successfully.");

        } catch (Exception e) {
            showError("Add Instructor Error", e.getMessage());
        }
    }

    private void updateInstructor() {
        try {
            if (instructorIDField.getText().isBlank()) {
                showError("Selection Error", "Select an instructor from the table first.");
                return;
            }

            int instructorID = Integer.parseInt(instructorIDField.getText());

            instructorDAO.updateInstructor(
                    instructorID,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    expertiseField.getText()
            );

            loadInstructors();
            clearForm();
            showInfo("Success", "Instructor updated successfully.");

        } catch (Exception e) {
            showError("Update Instructor Error", e.getMessage());
        }
    }

    private void deleteInstructor() {
        try {
            if (instructorIDField.getText().isBlank()) {
                showError("Selection Error", "Select an instructor from the table first.");
                return;
            }

            int instructorID = Integer.parseInt(instructorIDField.getText());

            instructorDAO.deleteInstructor(instructorID);

            loadInstructors();
            clearForm();
            showInfo("Success", "Instructor deleted successfully.");

        } catch (Exception e) {
            showError("Delete Instructor Error", e.getMessage());
        }
    }

    private void fillForm(Instructor instructor) {
        instructorIDField.setText(String.valueOf(instructor.getInstructorID()));
        firstNameField.setText(instructor.getFirstName());
        lastNameField.setText(instructor.getLastName());
        emailField.setText(instructor.getEmail());
        expertiseField.setText(instructor.getExpertise());
    }

    private void clearForm() {
        instructorIDField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        expertiseField.clear();
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
