package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class ResultController {

    @FXML
    private Label studentSummary;

    @FXML
    private VBox coursesBox;

    @FXML
    private Label gpaLabel;

    @FXML
    private Label totalCreditsLabel;

    public void initData(List<Course> courses, double gpa, double totalCredits) {
        studentSummary.setText("GPA Report\n");
        coursesBox.getChildren().clear();
        for (Course c : courses) {
            Label l = new Label(String.format("%s (%s) -- Credit: %.2f -- Grade: %s -- Teachers: %s, %s", c.getTitle(), c.getCode(), c.getCredit(), c.getGrade(), c.getTeacher1(), c.getTeacher2()));
            coursesBox.getChildren().add(l);
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