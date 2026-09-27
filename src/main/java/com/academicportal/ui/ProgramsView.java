package com.academicportal.ui;

import com.academicportal.dao.CategoryDAO;
import com.academicportal.dao.InstructorDAO;
import com.academicportal.dao.ProgramDAO;
import com.academicportal.model.Category;
import com.academicportal.model.Instructor;
import com.academicportal.model.Program;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProgramsView extends VBox {

    private final TextField programIDField = new TextField();
    private final TextField programTitleField = new TextField();
    private final ComboBox<String> difficultyComboBox = new ComboBox<>();
    private final TextField registrationFeeField = new TextField();
    private final ComboBox<String> statusComboBox = new ComboBox<>();
    private final ComboBox<Instructor> instructorComboBox = new ComboBox<>();
    private final ComboBox<Category> categoryComboBox = new ComboBox<>();

    private final ComboBox<Category> filterCategoryComboBox = new ComboBox<>();
    private final ComboBox<String> filterDifficultyComboBox = new ComboBox<>();
    private final TextField minPriceField = new TextField();
    private final TextField maxPriceField = new TextField();

    private final TableView<Program> tableView = new TableView<>();
    private final ObservableList<Program> programs = FXCollections.observableArrayList();

    private final ProgramDAO programDAO = new ProgramDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private final Label statusLabel = new Label();

    public ProgramsView() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Programs Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        setupFields();
        setupComboBoxes();

        GridPane form = createForm();
        HBox actionButtons = createActionButtons();
        HBox filterBox = createFilterBox();

        statusLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        createTable();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selectedProgram) -> {
                    if (selectedProgram != null) {
                        fillForm(selectedProgram);
                    }
                }
        );

        getChildren().addAll(title, form, actionButtons, new Separator(), filterBox, statusLabel, tableView);

        loadComboBoxData();
        loadPrograms();
    }

    private void setupFields() {
        programIDField.setPromptText("Program ID");
        programIDField.setEditable(false);

        programTitleField.setPromptText("Program Title");
        registrationFeeField.setPromptText("Registration Fee");

        minPriceField.setPromptText("Min Price");
        maxPriceField.setPromptText("Max Price");
    }

    private void setupComboBoxes() {
        difficultyComboBox.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));

        filterDifficultyComboBox.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        filterDifficultyComboBox.setPromptText("Difficulty");

        setupCategoryComboBox(categoryComboBox);
        setupCategoryComboBox(filterCategoryComboBox);
        setupInstructorComboBox(instructorComboBox);

        categoryComboBox.setPromptText("Select Category");
        instructorComboBox.setPromptText("Select Instructor");
        filterCategoryComboBox.setPromptText("Category");
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Program ID:"), 0, 0);
        form.add(programIDField, 1, 0);

        form.add(new Label("Program Title:"), 0, 1);
        form.add(programTitleField, 1, 1);

        form.add(new Label("Difficulty:"), 0, 2);
        form.add(difficultyComboBox, 1, 2);

        form.add(new Label("Registration Fee:"), 0, 3);
        form.add(registrationFeeField, 1, 3);

        form.add(new Label("Status:"), 2, 0);
        form.add(statusComboBox, 3, 0);

        form.add(new Label("Instructor:"), 2, 1);
        form.add(instructorComboBox, 3, 1);

        form.add(new Label("Category:"), 2, 2);
        form.add(categoryComboBox, 3, 2);

        return form;
    }

    private HBox createActionButtons() {
        Button addButton = new Button("Add Program");
        Button updateButton = new Button("Update Program");
        Button deleteButton = new Button("Delete Program");
        Button loadButton = new Button("Load Programs");
        Button clearButton = new Button("Clear");

        addButton.setOnAction(event -> addProgram());
        updateButton.setOnAction(event -> updateProgram());
        deleteButton.setOnAction(event -> deleteProgram());
        loadButton.setOnAction(event -> loadPrograms());
        clearButton.setOnAction(event -> clearForm());

        return new HBox(10, addButton, updateButton, deleteButton, loadButton, clearButton);
    }

    private HBox createFilterBox() {
        Button filterButton = new Button("Filter Programs");
        Button clearFiltersButton = new Button("Clear Filters");

        filterButton.setOnAction(event -> filterPrograms());
        clearFiltersButton.setOnAction(event -> clearFilters());

        return new HBox(
                10,
                new Label("Filters:"),
                filterCategoryComboBox,
                filterDifficultyComboBox,
                minPriceField,
                maxPriceField,
                filterButton,
                clearFiltersButton
        );
    }

    private void createTable() {
        TableColumn<Program, Integer> idColumn = new TableColumn<>("ProgramID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("programID"));

        TableColumn<Program, String> titleColumn = new TableColumn<>("ProgramTitle");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("programTitle"));
        titleColumn.setPrefWidth(180);

        TableColumn<Program, String> difficultyColumn = new TableColumn<>("DifficultyLevel");
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficultyLevel"));
        difficultyColumn.setPrefWidth(120);

        TableColumn<Program, Double> feeColumn = new TableColumn<>("RegistrationFee");
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("registrationFee"));
        feeColumn.setPrefWidth(120);

        TableColumn<Program, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(100);

        TableColumn<Program, String> instructorColumn = new TableColumn<>("InstructorName");
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructorName"));
        instructorColumn.setPrefWidth(160);

        TableColumn<Program, String> categoryColumn = new TableColumn<>("CategoryName");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryColumn.setPrefWidth(160);

        tableView.getColumns().addAll(
                idColumn,
                titleColumn,
                difficultyColumn,
                feeColumn,
                statusColumn,
                instructorColumn,
                categoryColumn
        );

        tableView.setItems(programs);
        tableView.setPrefHeight(350);
    }

    private void loadComboBoxData() {
        try {
            ObservableList<Category> categories = FXCollections.observableArrayList(categoryDAO.getAllCategories());
            ObservableList<Instructor> instructors = FXCollections.observableArrayList(instructorDAO.getAllInstructors());

            categoryComboBox.setItems(categories);
            filterCategoryComboBox.setItems(FXCollections.observableArrayList(categories));
            instructorComboBox.setItems(instructors);

        } catch (Exception e) {
            showError("Load ComboBox Data Error", e.getMessage());
        }
    }

    private void loadPrograms() {
        try {
            programs.setAll(programDAO.getAllPrograms());
            tableView.refresh();
            statusLabel.setText("Loaded " + programs.size() + " programs successfully.");
        } catch (Exception e) {
            showError("Load Programs Error", e.getMessage());
        }
    }

    private void addProgram() {
        try {
            if (!isFormValid()) {
                return;
            }

            programDAO.addProgram(
                    programTitleField.getText(),
                    difficultyComboBox.getValue(),
                    Double.parseDouble(registrationFeeField.getText()),
                    statusComboBox.getValue(),
                    instructorComboBox.getValue().getInstructorID(),
                    categoryComboBox.getValue().getCategoryID()
            );

            loadPrograms();
            clearForm();
            showInfo("Success", "Program added successfully.");

        } catch (Exception e) {
            showError("Add Program Error", e.getMessage());
        }
    }

    private void updateProgram() {
        try {
            if (programIDField.getText().isBlank()) {
                showError("Selection Error", "Select a program from the table first.");
                return;
            }

            if (!isFormValid()) {
                return;
            }

            int programID = Integer.parseInt(programIDField.getText());

            programDAO.updateProgram(
                    programID,
                    programTitleField.getText(),
                    difficultyComboBox.getValue(),
                    Double.parseDouble(registrationFeeField.getText()),
                    statusComboBox.getValue(),
                    instructorComboBox.getValue().getInstructorID(),
                    categoryComboBox.getValue().getCategoryID()
            );

            loadPrograms();
            clearForm();
            showInfo("Success", "Program updated successfully.");

        } catch (Exception e) {
            showError("Update Program Error", e.getMessage());
        }
    }

    private void deleteProgram() {
        try {
            if (programIDField.getText().isBlank()) {
                showError("Selection Error", "Select a program from the table first.");
                return;
            }

            int programID = Integer.parseInt(programIDField.getText());

            programDAO.deleteProgram(programID);

            loadPrograms();
            clearForm();
            showInfo("Success", "Program deleted successfully.");

        } catch (Exception e) {
            showError("Delete Program Error", e.getMessage());
        }
    }

    private void filterPrograms() {
        try {
            Integer categoryID = filterCategoryComboBox.getValue() == null
                    ? null
                    : filterCategoryComboBox.getValue().getCategoryID();

            String difficulty = filterDifficultyComboBox.getValue();

            Double minPrice = parseOptionalDouble(minPriceField.getText(), "Min Price");
            Double maxPrice = parseOptionalDouble(maxPriceField.getText(), "Max Price");

            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                showError("Validation Error", "Min Price cannot be greater than Max Price.");
                return;
            }

            programs.setAll(programDAO.filterPrograms(categoryID, difficulty, minPrice, maxPrice));
            tableView.refresh();
            statusLabel.setText("Filtered result: " + programs.size() + " programs.");

        } catch (Exception e) {
            showError("Filter Programs Error", e.getMessage());
        }
    }

    private void clearFilters() {
        filterCategoryComboBox.getSelectionModel().clearSelection();
        filterDifficultyComboBox.getSelectionModel().clearSelection();
        minPriceField.clear();
        maxPriceField.clear();
        loadPrograms();
    }

    private boolean isFormValid() {
        if (programTitleField.getText().isBlank()) {
            showError("Validation Error", "Program Title is required.");
            return false;
        }

        if (difficultyComboBox.getValue() == null) {
            showError("Validation Error", "Difficulty is required.");
            return false;
        }

        if (registrationFeeField.getText().isBlank()) {
            showError("Validation Error", "Registration Fee is required.");
            return false;
        }

        try {
            double fee = Double.parseDouble(registrationFeeField.getText());

            if (fee < 0) {
                showError("Validation Error", "Registration Fee cannot be negative.");
                return false;
            }

        } catch (NumberFormatException e) {
            showError("Validation Error", "Registration Fee must be a valid number.");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            showError("Validation Error", "Status is required.");
            return false;
        }

        if (instructorComboBox.getValue() == null) {
            showError("Validation Error", "Instructor is required.");
            return false;
        }

        if (categoryComboBox.getValue() == null) {
            showError("Validation Error", "Category is required.");
            return false;
        }

        return true;
    }

    private Double parseOptionalDouble(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private void fillForm(Program program) {
        programIDField.setText(String.valueOf(program.getProgramID()));
        programTitleField.setText(program.getProgramTitle());
        difficultyComboBox.setValue(program.getDifficultyLevel());
        registrationFeeField.setText(String.valueOf(program.getRegistrationFee()));
        statusComboBox.setValue(program.getStatus());

        selectInstructorByID(program.getInstructorID());
        selectCategoryByID(program.getCategoryID());
    }

    private void selectInstructorByID(int instructorID) {
        for (Instructor instructor : instructorComboBox.getItems()) {
            if (instructor.getInstructorID() == instructorID) {
                instructorComboBox.setValue(instructor);
                return;
            }
        }
    }

    private void selectCategoryByID(int categoryID) {
        for (Category category : categoryComboBox.getItems()) {
            if (category.getCategoryID() == categoryID) {
                categoryComboBox.setValue(category);
                return;
            }
        }
    }

    private void clearForm() {
        programIDField.clear();
        programTitleField.clear();
        difficultyComboBox.getSelectionModel().clearSelection();
        registrationFeeField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        instructorComboBox.getSelectionModel().clearSelection();
        categoryComboBox.getSelectionModel().clearSelection();
        tableView.getSelectionModel().clearSelection();
    }

    private void setupCategoryComboBox(ComboBox<Category> comboBox) {
        comboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? null : category.getCategoryName());
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? null : category.getCategoryName());
            }
        });
    }

    private void setupInstructorComboBox(ComboBox<Instructor> comboBox) {
        comboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Instructor instructor, boolean empty) {
                super.updateItem(instructor, empty);
                setText(empty || instructor == null
                        ? null
                        : instructor.getFirstName() + " " + instructor.getLastName());
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Instructor instructor, boolean empty) {
                super.updateItem(instructor, empty);
                setText(empty || instructor == null
                        ? null
                        : instructor.getFirstName() + " " + instructor.getLastName());
            }
        });
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