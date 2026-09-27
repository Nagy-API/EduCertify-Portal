package com.academicportal;

import com.academicportal.ui.CategoriesView;
import com.academicportal.ui.CredentialsView;
import com.academicportal.ui.EnrollmentView;
import com.academicportal.ui.InstructorsView;
import com.academicportal.ui.ProgramsView;
import com.academicportal.ui.ProgressView;
import com.academicportal.ui.ReportsView;
import com.academicportal.ui.StudentsView;
import com.academicportal.ui.UnitsView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane root;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.setLeft(createSidebar());
        root.setCenter(createWelcomeScreen());

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Academic Portal App");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label appTitle = new Label("Academic Portal");
        appTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button students = createSidebarButton("Students");
        Button instructors = createSidebarButton("Instructors");
        Button categories = createSidebarButton("Categories");
        Button programs = createSidebarButton("Programs");
        Button units = createSidebarButton("Units");
        Button enrollment = createSidebarButton("Enrollment");
        Button progress = createSidebarButton("Progress");
        Button credentials = createSidebarButton("Credentials");
        Button reports = createSidebarButton("Reports");

        students.setOnAction(e -> root.setCenter(new StudentsView()));
        instructors.setOnAction(e -> root.setCenter(new InstructorsView()));
        categories.setOnAction(e -> root.setCenter(new CategoriesView()));
        programs.setOnAction(e -> root.setCenter(new ProgramsView()));
        units.setOnAction(e -> root.setCenter(new UnitsView()));
        enrollment.setOnAction(e -> root.setCenter(new EnrollmentView()));
        progress.setOnAction(e -> root.setCenter(new ProgressView()));
        credentials.setOnAction(e -> root.setCenter(new CredentialsView()));
        reports.setOnAction(e -> root.setCenter(new ReportsView()));

        sidebar.getChildren().addAll(
                appTitle, students, instructors, categories, programs,
                units, enrollment, progress, credentials, reports
        );

        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(
                "-fx-background-color: #34495e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10;"
        );
        return button;
    }

    private VBox createWelcomeScreen() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("Welcome to Academic Portal App");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label("Use the sidebar to navigate between screens.");
        subtitle.setStyle("-fx-font-size: 16px;");

        box.getChildren().addAll(title, subtitle);
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}