package com.academicportal.ui;

import com.academicportal.dao.CredentialDAO;
import com.academicportal.dao.ProgramDAO;
import com.academicportal.dao.StudentDAO;
import com.academicportal.model.Credential;
import com.academicportal.model.Program;
import com.academicportal.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CredentialsView extends VBox {

    private final ComboBox<String> studentBox = new ComboBox<>();
    private final ComboBox<String> programBox = new ComboBox<>();
    private final TextField codeField = new TextField();

    private final TableView<Credential> table = new TableView<>();
    private final ObservableList<Credential> data = FXCollections.observableArrayList();

    private final CredentialDAO credentialDAO = new CredentialDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ProgramDAO programDAO = new ProgramDAO();

    private final Label status = new Label();

    public CredentialsView() {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Credentials Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        studentBox.setPromptText("Student");
        programBox.setPromptText("Program");
        codeField.setPromptText("Verification Code");

        Button issueBtn = new Button("Issue Credential");
        Button verifyBtn = new Button("Verify Credential");
        Button loadBtn = new Button("Load Credentials");
        Button clearBtn = new Button("Clear");

        issueBtn.setOnAction(e -> issueCredential());
        verifyBtn.setOnAction(e -> verifyCredential());
        loadBtn.setOnAction(e -> loadCredentials());
        clearBtn.setOnAction(e -> clear());

        createTable();
        loadComboBoxes();
        loadCredentials();

        HBox form = new HBox(10, studentBox, programBox, codeField);
        HBox buttons = new HBox(10, issueBtn, verifyBtn, loadBtn, clearBtn);

        getChildren().addAll(title, form, buttons, status, table);
    }

    private void createTable() {
        addColumn("CredentialID", "credentialID");
        addColumn("VerificationCode", "verificationCode");
        addColumn("GrantedDate", "grantedDate");
        addColumn("StudentName", "studentName");
        addColumn("ProgramTitle", "programTitle");

        table.setItems(data);
        table.setPrefHeight(380);
    }

    private void addColumn(String title, String property) {
        TableColumn<Credential, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(160);
        table.getColumns().add(column);
    }

    private void loadComboBoxes() {
        try {
            for (Student s : studentDAO.getAllStudents()) {
                studentBox.getItems().add(s.getStudentID() + " - " + s.getFirstName() + " " + s.getLastName());
            }

            for (Program p : programDAO.getAllPrograms()) {
                programBox.getItems().add(p.getProgramID() + " - " + p.getProgramTitle());
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadCredentials() {
        try {
            data.setAll(credentialDAO.getAllCredentials());
            status.setText("Loaded " + data.size() + " credentials.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void issueCredential() {
        try {
            if (studentBox.getValue() == null || programBox.getValue() == null) {
                showError("Select student and program first.");
                return;
            }

            String code = codeField.getText().trim();

            if (code.isBlank()) {
                code = "CERT-" + System.currentTimeMillis();
                codeField.setText(code);
            }

            credentialDAO.issueCredential(
                    getID(studentBox.getValue()),
                    getID(programBox.getValue()),
                    code
            );

            loadCredentials();
            status.setText("Credential issued successfully.");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void verifyCredential() {
        try {
            if (codeField.getText().isBlank()) {
                showError("Enter Verification Code first.");
                return;
            }

            data.setAll(credentialDAO.verifyCredential(codeField.getText().trim()));
            status.setText(data.isEmpty() ? "Credential not found." : "Credential verified.");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private int getID(String value) {
        return Integer.parseInt(value.split(" - ")[0]);
    }

    private void clear() {
        studentBox.getSelectionModel().clearSelection();
        programBox.getSelectionModel().clearSelection();
        codeField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}