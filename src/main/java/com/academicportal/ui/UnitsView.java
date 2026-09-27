package com.academicportal.ui;

import com.academicportal.dao.ProgramDAO;
import com.academicportal.dao.UnitDAO;
import com.academicportal.model.Program;
import com.academicportal.model.Unit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UnitsView extends VBox {

    private final TextField unitIDField = new TextField();
    private final TextField unitTitleField = new TextField();
    private final TextField estimatedCompletionTimeField = new TextField();
    private final TextField sequenceOrderField = new TextField();
    private final ComboBox<Program> programComboBox = new ComboBox<>();

    private final TableView<Unit> tableView = new TableView<>();
    private final ObservableList<Unit> units = FXCollections.observableArrayList();

    private final UnitDAO unitDAO = new UnitDAO();
    private final ProgramDAO programDAO = new ProgramDAO();

    private final Label statusLabel = new Label();

    public UnitsView() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Units Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        setupFields();
        setupProgramComboBox();

        GridPane form = createForm();

        Button addButton = new Button("Add Unit");
        Button updateButton = new Button("Update Unit");
        Button deleteButton = new Button("Delete Unit");
        Button loadButton = new Button("Load Units");
        Button clearButton = new Button("Clear");

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, loadButton, clearButton);

        statusLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        createTable();

        addButton.setOnAction(event -> addUnit());
        updateButton.setOnAction(event -> updateUnit());
        deleteButton.setOnAction(event -> deleteUnit());
        loadButton.setOnAction(event -> loadUnits());
        clearButton.setOnAction(event -> clearForm());

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedUnit) -> {
                    if (selectedUnit != null) {
                        fillForm(selectedUnit);
                    }
                }
        );

        getChildren().addAll(title, form, buttons, statusLabel, tableView);

        loadProgramsIntoComboBox();
        loadUnits();
    }

    private void setupFields() {
        unitIDField.setPromptText("Unit ID");
        unitIDField.setEditable(false);

        unitTitleField.setPromptText("Unit Title");
        estimatedCompletionTimeField.setPromptText("Estimated Completion Time");
        sequenceOrderField.setPromptText("Sequence Order");

        programComboBox.setPromptText("Select Program");
    }

    private void setupProgramComboBox() {
        programComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Program program, boolean empty) {
                super.updateItem(program, empty);
                setText(empty || program == null ? null : program.getProgramTitle());
            }
        });

        programComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Program program, boolean empty) {
                super.updateItem(program, empty);
                setText(empty || program == null ? null : program.getProgramTitle());
            }
        });
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Unit ID:"), 0, 0);
        form.add(unitIDField, 1, 0);

        form.add(new Label("Unit Title:"), 0, 1);
        form.add(unitTitleField, 1, 1);

        form.add(new Label("Estimated Time:"), 0, 2);
        form.add(estimatedCompletionTimeField, 1, 2);

        form.add(new Label("Sequence Order:"), 2, 0);
        form.add(sequenceOrderField, 3, 0);

        form.add(new Label("Program:"), 2, 1);
        form.add(programComboBox, 3, 1);

        return form;
    }

    private void createTable() {
        TableColumn<Unit, Integer> idColumn = new TableColumn<>("UnitID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("unitID"));

        TableColumn<Unit, String> titleColumn = new TableColumn<>("UnitTitle");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("unitTitle"));
        titleColumn.setPrefWidth(180);

        TableColumn<Unit, Integer> timeColumn = new TableColumn<>("EstimatedCompletionTime");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedCompletionTime"));
        timeColumn.setPrefWidth(180);

        TableColumn<Unit, Integer> sequenceColumn = new TableColumn<>("SequenceOrder");
        sequenceColumn.setCellValueFactory(new PropertyValueFactory<>("sequenceOrder"));
        sequenceColumn.setPrefWidth(130);

        TableColumn<Unit, String> programColumn = new TableColumn<>("ProgramTitle");
        programColumn.setCellValueFactory(new PropertyValueFactory<>("programTitle"));
        programColumn.setPrefWidth(220);

        tableView.getColumns().addAll(
                idColumn,
                titleColumn,
                timeColumn,
                sequenceColumn,
                programColumn
        );

        tableView.setItems(units);
        tableView.setPrefHeight(350);
    }

    private void loadProgramsIntoComboBox() {
        try {
            programComboBox.setItems(FXCollections.observableArrayList(programDAO.getAllPrograms()));
        } catch (Exception e) {
            showError("Load Programs Error", e.getMessage());
        }
    }

    private void loadUnits() {
        try {
            units.setAll(unitDAO.getAllUnits());
            tableView.refresh();
            statusLabel.setText("Loaded " + units.size() + " units successfully.");
        } catch (Exception e) {
            showError("Load Units Error", e.getMessage());
        }
    }

    private void addUnit() {
        try {
            if (!isFormValid()) {
                return;
            }

            unitDAO.addUnit(
                    unitTitleField.getText(),
                    Integer.parseInt(estimatedCompletionTimeField.getText()),
                    Integer.parseInt(sequenceOrderField.getText()),
                    programComboBox.getValue().getProgramID()
            );

            loadUnits();
            clearForm();
            showInfo("Success", "Unit added successfully.");

        } catch (Exception e) {
            showError("Add Unit Error", e.getMessage());
        }
    }

    private void updateUnit() {
        try {
            if (unitIDField.getText().isBlank()) {
                showError("Selection Error", "Select a unit from the table first.");
                return;
            }

            if (!isFormValid()) {
                return;
            }

            int unitID = Integer.parseInt(unitIDField.getText());

            unitDAO.updateUnit(
                    unitID,
                    unitTitleField.getText(),
                    Integer.parseInt(estimatedCompletionTimeField.getText()),
                    Integer.parseInt(sequenceOrderField.getText()),
                    programComboBox.getValue().getProgramID()
            );

            loadUnits();
            clearForm();
            showInfo("Success", "Unit updated successfully.");

        } catch (Exception e) {
            showError("Update Unit Error", e.getMessage());
        }
    }

    private void deleteUnit() {
        try {
            if (unitIDField.getText().isBlank()) {
                showError("Selection Error", "Select a unit from the table first.");
                return;
            }

            int unitID = Integer.parseInt(unitIDField.getText());

            unitDAO.deleteUnit(unitID);

            loadUnits();
            clearForm();
            showInfo("Success", "Unit deleted successfully.");

        } catch (Exception e) {
            showError("Delete Unit Error", e.getMessage());
        }
    }

    private boolean isFormValid() {
        if (unitTitleField.getText().isBlank()) {
            showError("Validation Error", "Unit Title is required.");
            return false;
        }

        if (!isPositiveInteger(estimatedCompletionTimeField.getText())) {
            showError("Validation Error", "Estimated Completion Time must be a positive number.");
            return false;
        }

        if (!isPositiveInteger(sequenceOrderField.getText())) {
            showError("Validation Error", "Sequence Order must be a positive number.");
            return false;
        }

        if (programComboBox.getValue() == null) {
            showError("Validation Error", "Program is required.");
            return false;
        }

        return true;
    }

    private boolean isPositiveInteger(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void fillForm(Unit unit) {
        unitIDField.setText(String.valueOf(unit.getUnitID()));
        unitTitleField.setText(unit.getUnitTitle());
        estimatedCompletionTimeField.setText(String.valueOf(unit.getEstimatedCompletionTime()));
        sequenceOrderField.setText(String.valueOf(unit.getSequenceOrder()));

        selectProgramByID(unit.getProgramID());
    }

    private void selectProgramByID(int programID) {
        for (Program program : programComboBox.getItems()) {
            if (program.getProgramID() == programID) {
                programComboBox.setValue(program);
                return;
            }
        }
    }

    private void clearForm() {
        unitIDField.clear();
        unitTitleField.clear();
        estimatedCompletionTimeField.clear();
        sequenceOrderField.clear();
        programComboBox.getSelectionModel().clearSelection();
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