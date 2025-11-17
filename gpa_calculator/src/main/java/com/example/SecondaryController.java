package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

//Info entry controller
public class SecondaryController {

    @FXML
    private ChoiceBox<Double> course_credit;
    @FXML
    private ChoiceBox<String> course_grade;

    @FXML
    private TextField course_title;
    @FXML
    private TextField course_code;
    @FXML
    private TextField course_teacher1;
    @FXML
    private TextField course_teacher2;

    @FXML
    private List<Course> courses = new ArrayList<>();
    @FXML
    private Label gpaLabel;
    @FXML
    private Label enteredCreditsLabel;
    @FXML
    private Button calculateButton;
    private Map<String, Double> gradePointMap = new HashMap<>();

    @FXML
    public void initialize() {
        course_credit.getItems().addAll(4.0, 3.0, 2.0, 1.5, 0.75);
        course_credit.setValue(3.0);
        course_grade.getItems().addAll("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F");
        course_grade.setValue("A+");
        double gp = 4.00;
        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
        for (String g : grades) {
            gradePointMap.put(g, gp);
            gp -= 0.25;
        }
        checkCalculateEnabled();
    }

    @FXML
    private void add_course() {
        addCourse(true);
    }

    // course add kore
    private void addCourse(boolean showAlert) {
        String title = course_title.getText();
        String code = course_code.getText();
        String teacher1 = course_teacher1.getText();
        String teacher2 = course_teacher2.getText();
        Double credit = course_credit.getValue();
        String grade = course_grade.getValue();

        Course c = new Course(title, code, teacher1, teacher2, credit == null ? 0.0 : credit.doubleValue(), grade == null ? "F" : grade);
    // course store kore
    courses.add(c);
    updateEnteredCredits();
    checkCalculateEnabled();
        //ekta course add korar por popup message dekhabe
        if (showAlert) {
            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Course Added");
            a.setHeaderText(null);
            a.setContentText("Course added successfully.");
            a.showAndWait();
        }
        // ekta course add korar por form clear korbe
        course_title.clear();
        course_code.clear();
        course_teacher1.clear();
        course_teacher2.clear();
    }
   
    // course add korar por etar credit arr grade add krbe main result e
    private void updateEnteredCredits() {
        double sum = 0.0;
        for (Course c : courses) sum += c.getCredit();
        enteredCreditsLabel.setText(String.format("Earned Credits: %.2f", sum));
    }

    private void checkCalculateEnabled() {
        double sum = 0.0; for (Course c : courses) sum += c.getCredit();
        boolean enable = sum > 0.0001;
        calculateButton.setDisable(!enable);
    }
    @FXML
    private void onCalculatePressed() {
        System.out.println("onCalculatePressed function called");
        if (courses.isEmpty()) {
            String currTitle = course_title.getText();
            if (currTitle != null && !currTitle.isBlank()) {
                System.out.println("Auto-adding current course entry before calculate");
                addCourse(false); 
            }
        }

        // kono course input na dile popup message dekhabe
        if (courses.isEmpty()) {
            Alert a = new Alert(AlertType.WARNING);
            a.setTitle("No Courses");
            a.setHeaderText(null);
            a.setContentText("Please add a course before calculating GPA.");
            a.showAndWait();
            return;
        }
        //course add krle gpa add krbe final result e
        double totalWeighted = 0.0;
        double totalCredits = 0.0;
        for (Course c : courses) {
            double credit = c.getCredit();
            Double gp = gradePointMap.getOrDefault(c.getGrade(), null);
            if (gp == null) continue;
            totalWeighted += credit * gp;
            totalCredits += credit;
        }
    if (totalCredits == 0) return; // 1/0=INF na ashar jnno;
        double gpa = totalWeighted / totalCredits;

        // load
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("result.fxml"));
            Parent root = loader.load();
            ResultController rc = loader.getController();
            rc.initData(List.copyOf(courses), gpa, totalCredits);
            App.setRootParent(root);
        } catch (IOException e) {
            e.printStackTrace();
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Can't open results");
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
    }
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    
}