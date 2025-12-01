package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.SQLException;

public class ResultController {

    @FXML
    private Label studentSummary;

    @FXML
    private VBox coursesBox;

    @FXML
    private Label gpaLabel;

    @FXML
    private Label totalCreditsLabel;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final DatabaseHelper db = DatabaseHelper.getInstance();

    public void initData(List<Course> courses, double gpa, double totalCredits) {
        studentSummary.setText("GPA Report\n");
        coursesBox.getChildren().clear();
        for (Course c : courses) {
            Label l = new Label(String.format("%s (%s) -- Credit: %.2f -- Grade: %s -- Teachers: %s, %s", c.getTitle(), c.getCode(), c.getCredit(), c.getGrade(), c.getTeacher1(), c.getTeacher2()));
            Button editBtn = new Button("Edit");
            editBtn.setOnAction(ev -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
                    Parent root = loader.load();
                    SecondaryController sc = loader.getController();
                    sc.editCourse(c);
                    App.setRootParent(root);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            Button delBtn = new Button("Delete");
            delBtn.setOnAction(ev -> {
               
                executor.execute(() -> {
                    try {
                        boolean ok = db.deleteCourseByCode(c.getCode());
                        if (ok) {
                            Platform.runLater(() -> {
                                coursesBox.getChildren().removeIf(node -> node instanceof HBox && ((HBox) node).getChildren().contains(l));
                                
                                double newTotalCredits = Math.max(0.0, totalCredits - c.getCredit());
                                totalCreditsLabel.setText(String.format("Total Credits: %.2f", newTotalCredits));
                                Label info = new Label("Course " + c.getCode() + " deleted. Re-run calculate to refresh GPA.");
                                coursesBox.getChildren().add(info);
                            });
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            Label err = new Label("Failed to delete: " + ex.getMessage());
                            coursesBox.getChildren().add(err);
                        });
                    }
                });
            });
            HBox row = new HBox(8, l, editBtn, delBtn);
            coursesBox.getChildren().add(row);
        }
        gpaLabel.setText(String.format("GPA: %.2f", gpa));
        totalCreditsLabel.setText(String.format("Total Credits: %.2f", totalCredits));
    }

    @FXML
    private void onBackToHome() {
        try {
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}