package com.academicportal.ui;

import com.academicportal.dao.ReportsDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ReportsView extends VBox {

    private final ReportsDAO reportsDAO = new ReportsDAO();
    private final TableView<ObservableList<String>> table = new TableView<>();
    private final Label status = new Label("Choose a report.");

    public ReportsView() {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Reports Screen");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        FlowPane buttons = createReportButtons();

        table.setPrefHeight(420);
        status.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        getChildren().addAll(title, buttons, status, table);
    }

    private FlowPane createReportButtons() {
        FlowPane pane = new FlowPane(10, 10);

        String[] names = {
                "Most Popular Program",
                "Programs With No Signups Last Month",
                "Top Instructor by Signups Last Month",
                "Students With No Completed Units Last Month",
                "Active Programs Under Each Category",
                "Student Credentials Count"
        };

        for (int i = 0; i < names.length; i++) {
            int reportNumber = i + 1;
            Button button = new Button(names[i]);
            button.setOnAction(e -> loadReport(reportNumber, names[reportNumber - 1]));
            pane.getChildren().add(button);
        }

        return pane;
    }

    private void loadReport(int reportNumber, String reportName) {
        try {
            ReportsDAO.ReportResult result = reportsDAO.runReport(reportNumber);

            table.getColumns().clear();
            table.getItems().clear();

            createColumns(result.columns);
            fillRows(result.rows);

            status.setText(reportName + " - Rows: " + result.rows.size());

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void createColumns(List<String> columns) {
        for (int i = 0; i < columns.size(); i++) {
            int index = i;

            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columns.get(i));
            column.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get(index))
            );
            column.setPrefWidth(180);

            table.getColumns().add(column);
        }
    }

    private void fillRows(List<List<String>> rows) {
        ObservableList<ObservableList<String>> tableRows = FXCollections.observableArrayList();

        for (List<String> row : rows) {
            tableRows.add(FXCollections.observableArrayList(row));
        }

        table.setItems(tableRows);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Reports Error");
        alert.setHeaderText("Reports Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}