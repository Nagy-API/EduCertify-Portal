package com.academicportal.ui;

import com.academicportal.dao.StudentDAO;
import com.academicportal.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StudentsView extends VBox {

    private final TextField studentIDField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField phoneField = new TextField();

    private final TableView<Student> tableView = new TableView<>();
    private final ObservableList<Student> students = FXCollections.observableArrayList();

    private final StudentDAO studentDAO = new StudentDAO();
    private final Label statusLabel = new Label();
    public StudentsView() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Students Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        studentIDField.setPromptText("Student ID");
        studentIDField.setEditable(false);

        firstNameField.setPromptText("First Name");
        lastNameField.setPromptText("Last Name");
        emailField.setPromptText("Email");
        phoneField.setPromptText("Phone");

        GridPane form = createForm();

        Button addButton = new Button("Add Student");
        Button updateButton = new Button("Update Student");
        Button deleteButton = new Button("Delete Student");
        Button loadButton = new Button("Load Students");
        Button clearButton = new Button("Clear");

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, loadButton, clearButton);
        statusLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        createTable();

        addButton.setOnAction(event -> addStudent());
        updateButton.setOnAction(event -> updateStudent());
        deleteButton.setOnAction(event -> deleteStudent());
        loadButton.setOnAction(event -> loadStudents());
        clearButton.setOnAction(event -> clearForm());

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedStudent) -> {
                    if (selectedStudent != null) {
                        fillForm(selectedStudent);
                    }
                }
        );

        getChildren().addAll(title, form, buttons, statusLabel, tableView);
        loadStudents();
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Student ID:"), 0, 0);
        form.add(studentIDField, 1, 0);

        form.add(new Label("First Name:"), 0, 1);
        form.add(firstNameField, 1, 1);

        form.add(new Label("Last Name:"), 0, 2);
        form.add(lastNameField, 1, 2);

        form.add(new Label("Email:"), 0, 3);
        form.add(emailField, 1, 3);

        form.add(new Label("Phone:"), 0, 4);
        form.add(phoneField, 1, 4);

        return form;
    }

    private void createTable() {
        TableColumn<Student, Integer> idColumn = new TableColumn<>("StudentID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentID"));

        TableColumn<Student, String> firstNameColumn = new TableColumn<>("FirstName");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> lastNameColumn = new TableColumn<>("LastName");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Student, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Student, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Student, java.time.LocalDateTime> dateColumn = new TableColumn<>("RegistrationDate");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        tableView.getColumns().addAll(
                idColumn,
                firstNameColumn,
                lastNameColumn,
                emailColumn,
                phoneColumn,
                dateColumn
        );

        tableView.setItems(students);
        tableView.setPrefHeight(300);
    }

    private void loadStudents() {
        try {
            students.setAll(studentDAO.getAllStudents());
            tableView.refresh();
            statusLabel.setText("Loaded " + students.size() + " students successfully.");
        } catch (Exception e) {
            showError("Load Students Error", e.getMessage());
        }
    }

    private void addStudent() {
        try {
            if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()) {
                showError("Validation Error", "First Name and Last Name are required.");
                return;
            }

            studentDAO.addStudent(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText()
            );

            loadStudents();
            clearForm();
            showInfo("Success", "Student added successfully.");
            statusLabel.setText("Student added and table reloaded.");
        } catch (Exception e) {
            showError("Add Student Error", e.getMessage());
        }
    }

    private void updateStudent() {
        try {
            if (studentIDField.getText().isBlank()) {
                showError("Selection Error", "Select a student from the table first.");
                return;
            }

            int studentID = Integer.parseInt(studentIDField.getText());

            studentDAO.updateStudent(
                    studentID,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    phoneField.getText()
            );

            loadStudents();
            clearForm();
            showInfo("Success", "Student updated successfully.");
            statusLabel.setText("Student updated and table reloaded.");
        } catch (Exception e) {
            showError("Update Student Error", e.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            if (studentIDField.getText().isBlank()) {
                showError("Selection Error", "Select a student from the table first.");
                return;
            }

            int studentID = Integer.parseInt(studentIDField.getText());

            studentDAO.deleteStudent(studentID);

            loadStudents();
            clearForm();
            showInfo("Success", "Student deleted successfully.");
            statusLabel.setText("Student deleted and table reloaded.");
        } catch (Exception e) {
            showError("Delete Student Error", e.getMessage());
        }
    }

    private void fillForm(Student student) {
        studentIDField.setText(String.valueOf(student.getStudentID()));
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
    }

    private void clearForm() {
        studentIDField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
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
