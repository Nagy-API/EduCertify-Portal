package com.academicportal.ui;

import com.academicportal.dao.EnrollmentDAO;
import com.academicportal.dao.ProgramDAO;
import com.academicportal.dao.StudentDAO;
import com.academicportal.model.Enrollment;
import com.academicportal.model.Program;
import com.academicportal.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EnrollmentView extends VBox {

    private final ComboBox<String> studentBox = new ComboBox<>();
    private final ComboBox<String> programBox = new ComboBox<>();
    private final TextField progressField = new TextField();
    private final ComboBox<String> statusBox = new ComboBox<>();

    private final TableView<Enrollment> table = new TableView<>();
    private final ObservableList<Enrollment> data = FXCollections.observableArrayList();

    private final StudentDAO studentDAO = new StudentDAO();
    private final ProgramDAO programDAO = new ProgramDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    private final Label message = new Label();

    public EnrollmentView() {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Enrollment Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        studentBox.setPromptText("Student");
        programBox.setPromptText("Program");
        progressField.setPromptText("Progress Percentage");
        statusBox.setPromptText("Completion Status");
        statusBox.setItems(FXCollections.observableArrayList(
                "In Progress",
                "Completed",
                "Dropped"
        ));

        Button enrollButton = new Button("Enroll Student");
        Button progressButton = new Button("Update Progress Percentage");
        Button statusButton = new Button("Update Completion Status");
        Button loadButton = new Button("Load Enrollments");
        Button clearButton = new Button("Clear");

        enrollButton.setOnAction(event -> enrollStudent());
        progressButton.setOnAction(event -> updateProgress());
        statusButton.setOnAction(event -> updateStatus());
        loadButton.setOnAction(event -> loadEnrollments());
        clearButton.setOnAction(event -> clearForm());

        HBox form = new HBox(10, studentBox, programBox, progressField, statusBox);
        HBox buttons = new HBox(10, enrollButton, progressButton, statusButton, loadButton, clearButton);

        createTable();
        loadComboBoxes();
        loadEnrollments();

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> {
                    if (selected != null) {
                        fillForm(selected);
                    }
                }
        );

        getChildren().addAll(title, form, buttons, message, table);
    }

    private void createTable() {
        addColumn("StudentID", "studentID");
        addColumn("StudentName", "studentName");
        addColumn("ProgramID", "programID");
        addColumn("ProgramTitle", "programTitle");
        addColumn("SignupDate", "signupDate");
        addColumn("ProgressPercentage", "progressPercentage");
        addColumn("CompletionStatus", "completionStatus");

        table.setItems(data);
        table.setPrefHeight(380);
    }

    private void addColumn(String title, String property) {
        TableColumn<Enrollment, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(150);
        table.getColumns().add(column);
    }

    private void loadComboBoxes() {
        try {
            studentBox.getItems().clear();
            programBox.getItems().clear();

            for (Student student : studentDAO.getAllStudents()) {
                studentBox.getItems().add(
                        student.getStudentID() + " - "
                                + student.getFirstName() + " "
                                + student.getLastName()
                );
            }

            for (Program program : programDAO.getAllPrograms()) {
                programBox.getItems().add(
                        program.getProgramID() + " - "
                                + program.getProgramTitle()
                );
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadEnrollments() {
        try {
            data.setAll(enrollmentDAO.getAllEnrollments());
            table.refresh();
            message.setText("Loaded " + data.size() + " enrollments.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void enrollStudent() {
        try {
            checkStudentAndProgram();
            checkStatus();

            enrollmentDAO.addEnrollment(
                    getID(studentBox.getValue()),
                    getID(programBox.getValue()),
                    getProgress(),
                    statusBox.getValue()
            );

            loadEnrollments();
            message.setText("Student enrolled successfully.");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void updateProgress() {
        try {
            checkStudentAndProgram();

            enrollmentDAO.updateProgressPercentage(
                    getID(studentBox.getValue()),
                    getID(programBox.getValue()),
                    getProgress()
            );

            loadEnrollments();
            message.setText("Progress updated successfully.");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void updateStatus() {
        try {
            checkStudentAndProgram();
            checkStatus();

            enrollmentDAO.updateCompletionStatus(
                    getID(studentBox.getValue()),
                    getID(programBox.getValue()),
                    statusBox.getValue()
            );

            loadEnrollments();
            message.setText("Completion status updated successfully.");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void checkStudentAndProgram() {
        if (studentBox.getValue() == null) {
            throw new IllegalArgumentException("Choose student first.");
        }

        if (programBox.getValue() == null) {
            throw new IllegalArgumentException("Choose program first.");
        }
    }

    private void checkStatus() {
        if (statusBox.getValue() == null) {
            throw new IllegalArgumentException("Choose completion status first.");
        }
    }

    private int getProgress() {
        try {
            int progress = Integer.parseInt(progressField.getText().trim());

            if (progress < 0 || progress > 100) {
                throw new IllegalArgumentException("Progress must be between 0 and 100.");
            }

            return progress;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Progress must be a valid number.");
        }
    }

    private int getID(String text) {
        return Integer.parseInt(text.split(" - ")[0]);
    }

    private void fillForm(Enrollment enrollment) {
        selectByID(studentBox, enrollment.getStudentID());
        selectByID(programBox, enrollment.getProgramID());
        progressField.setText(String.valueOf(enrollment.getProgressPercentage()));
        statusBox.setValue(enrollment.getCompletionStatus());
    }

    private void selectByID(ComboBox<String> box, int id) {
        for (String item : box.getItems()) {
            if (item.startsWith(id + " - ")) {
                box.setValue(item);
                return;
            }
        }
    }

    private void clearForm() {
        studentBox.getSelectionModel().clearSelection();
        programBox.getSelectionModel().clearSelection();
        progressField.clear();
        statusBox.getSelectionModel().clearSelection();
        table.getSelectionModel().clearSelection();
    }

    private void showError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Enrollment Error");
        alert.setHeaderText("Enrollment Error");
        alert.setContentText(text);
        alert.showAndWait();
    }
}