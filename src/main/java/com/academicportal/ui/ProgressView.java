package com.academicportal.ui;

import com.academicportal.dao.StudentDAO;
import com.academicportal.dao.UnitDAO;
import com.academicportal.dao.UnitProgressDAO;
import com.academicportal.model.Student;
import com.academicportal.model.Unit;
import com.academicportal.model.UnitProgress;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProgressView extends VBox {

    private final ComboBox<String> studentBox = new ComboBox<>();
    private final ComboBox<String> unitBox = new ComboBox<>();
    private final ComboBox<String> statusBox = new ComboBox<>();

    private final TableView<UnitProgress> table = new TableView<>();
    private final ObservableList<UnitProgress> data = FXCollections.observableArrayList();

    private final StudentDAO studentDAO = new StudentDAO();
    private final UnitDAO unitDAO = new UnitDAO();
    private final UnitProgressDAO progressDAO = new UnitProgressDAO();

    private final Label message = new Label();

    public ProgressView() {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Unit Progress Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        studentBox.setPromptText("Student");
        unitBox.setPromptText("Unit");
        statusBox.setPromptText("Unit Status");
        statusBox.setItems(FXCollections.observableArrayList(
                "Not Started",
                "In Progress",
                "Completed"
        ));

        Button completedButton = new Button("Mark Unit as Completed");
        Button updateButton = new Button("Update Unit Status");
        Button loadButton = new Button("Load Unit Progress");
        Button clearButton = new Button("Clear");

        completedButton.setOnAction(event -> markCompleted());
        updateButton.setOnAction(event -> updateStatus());
        loadButton.setOnAction(event -> loadProgress());
        clearButton.setOnAction(event -> clearForm());

        HBox form = new HBox(10, studentBox, unitBox, statusBox);
        HBox buttons = new HBox(10, completedButton, updateButton, loadButton, clearButton);

        createTable();
        loadComboBoxes();
        loadProgress();

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
        addColumn("UnitID", "unitID");
        addColumn("UnitTitle", "unitTitle");
        addColumn("ProgramTitle", "programTitle");
        addColumn("UnitStatus", "unitStatus");
        addColumn("CompletionDate", "completionDate");

        table.setItems(data);
        table.setPrefHeight(380);
    }

    private void addColumn(String title, String property) {
        TableColumn<UnitProgress, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(150);
        table.getColumns().add(column);
    }

    private void loadComboBoxes() {
        try {
            studentBox.getItems().clear();
            unitBox.getItems().clear();

            for (Student student : studentDAO.getAllStudents()) {
                studentBox.getItems().add(
                        student.getStudentID() + " - "
                                + student.getFirstName() + " "
                                + student.getLastName()
                );
            }

            for (Unit unit : unitDAO.getAllUnits()) {
                unitBox.getItems().add(
                        unit.getUnitID() + " - "
                                + unit.getProgramTitle() + " / "
                                + unit.getUnitTitle()
                );
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadProgress() {
        try {
            data.setAll(progressDAO.getAllUnitProgress());
            table.refresh();
            message.setText("Loaded " + data.size() + " unit progress records.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void markCompleted() {
        try {
            checkStudentAndUnit();

            progressDAO.markUnitAsCompleted(
                    getID(studentBox.getValue()),
                    getID(unitBox.getValue())
            );

            loadProgress();
            message.setText("Unit marked as completed.");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void updateStatus() {
        try {
            checkStudentAndUnit();

            if (statusBox.getValue() == null) {
                throw new IllegalArgumentException("Choose unit status first.");
            }

            progressDAO.updateUnitStatus(
                    getID(studentBox.getValue()),
                    getID(unitBox.getValue()),
                    statusBox.getValue()
            );

            loadProgress();
            message.setText("Unit status updated.");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void checkStudentAndUnit() {
        if (studentBox.getValue() == null) {
            throw new IllegalArgumentException("Choose student first.");
        }

        if (unitBox.getValue() == null) {
            throw new IllegalArgumentException("Choose unit first.");
        }
    }

    private int getID(String text) {
        return Integer.parseInt(text.split(" - ")[0]);
    }

    private void fillForm(UnitProgress progress) {
        selectByID(studentBox, progress.getStudentID());
        selectByID(unitBox, progress.getUnitID());
        statusBox.setValue(progress.getUnitStatus());
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
        unitBox.getSelectionModel().clearSelection();
        statusBox.getSelectionModel().clearSelection();
        table.getSelectionModel().clearSelection();
    }

    private void showError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Progress Error");
        alert.setHeaderText("Progress Error");
        alert.setContentText(text);
        alert.showAndWait();
    }
}