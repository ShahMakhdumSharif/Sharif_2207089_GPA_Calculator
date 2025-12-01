package com.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


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

    private final ObservableList<Course> courses = FXCollections.observableArrayList();
    @FXML
    private Label gpaLabel;
    @FXML
    private Label enteredCreditsLabel;
    @FXML
    private Button calculateButton;
    @FXML
    private Button addButton;
    private Map<String, Double> gradePointMap = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final DatabaseHelper db = DatabaseHelper.getInstance();
    
    private String editingCourseCode = null;

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
        executor.execute(() -> {
            try {
                db.initDB();
                List<Course> saved = db.fetchAllCourses();
                Platform.runLater(() -> {
                    courses.addAll(saved);
                    updateEnteredCredits();
                    checkCalculateEnabled();
                });
            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("Database Error");
                    a.setHeaderText(null);
                    a.setContentText("Failed to initialize/load database: " + e.getMessage());
                    a.showAndWait();
                });
            }
        });
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

        
        if (editingCourseCode != null) {
            
            c.setCode(editingCourseCode);
            executor.execute(() -> {
                try {
                    boolean ok = db.updateCourse(c);
                    if (ok) {
                        Platform.runLater(() -> {
                            
                            for (int i = 0; i < courses.size(); i++) {
                                if (editingCourseCode.equals(courses.get(i).getCode())) {
                                    courses.set(i, c);
                                    break;
                                }
                            }
                            editingCourseCode = null;
                            addButton.setText("Add Course");
                            updateEnteredCredits();
                            checkCalculateEnabled();
                            if (showAlert) {
                                Alert a = new Alert(AlertType.INFORMATION);
                                a.setTitle("Course Updated");
                                a.setHeaderText(null);
                                a.setContentText("Course updated successfully.");
                                a.showAndWait();
                            }
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setTitle("Database Error");
                        a.setHeaderText(null);
                        a.setContentText("Failed to update course: " + e.getMessage());
                        a.showAndWait();
                    });
                }
            });
        } else {
            executor.execute(() -> {
                try {
                    boolean ok = db.insertCourse(c);
                    if (ok) {
                        Platform.runLater(() -> {
                            courses.add(c);
                            updateEnteredCredits();
                            checkCalculateEnabled();
                            if (showAlert) {
                                Alert a = new Alert(AlertType.INFORMATION);
                                a.setTitle("Course Added");
                                a.setHeaderText(null);
                                a.setContentText("Course added successfully.");
                                a.showAndWait();
                            }
                            
                            course_title.clear();
                            course_code.clear();
                            course_teacher1.clear();
                            course_teacher2.clear();
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        Alert a = new Alert(AlertType.ERROR);
                        a.setTitle("Database Error");
                        a.setHeaderText(null);
                        a.setContentText("Failed to insert course: " + e.getMessage());
                        a.showAndWait();
                    });
                }
            });
        }
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

    @FXML
    private void exportJson() {
        
        List<Course> snapshot = List.copyOf(courses);
        executor.execute(() -> {
            String json = buildJson(snapshot);
            java.nio.file.Path out = java.nio.file.Path.of(System.getProperty("user.home"), "gpa_courses.json");
            try {
                java.nio.file.Files.writeString(out, json);
                Platform.runLater(() -> {
                    Alert a = new Alert(AlertType.INFORMATION);
                    a.setTitle("Export Complete");
                    a.setHeaderText(null);
                    a.setContentText("Exported " + snapshot.size() + " courses to " + out.toString());
                    a.showAndWait();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("Export Failed");
                    a.setHeaderText(null);
                    a.setContentText(e.getMessage());
                    a.showAndWait();
                });
            }
        });
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String buildJson(List<Course> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\n");
        sb.append("  \"courses\": [\n");
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            sb.append("    {");
            sb.append(String.format("\"title\": \"%s\", ", escape(c.getTitle())));
            sb.append(String.format("\"code\": \"%s\", ", escape(c.getCode())));
            sb.append(String.format("\"teacher1\": \"%s\", ", escape(c.getTeacher1())));
            sb.append(String.format("\"teacher2\": \"%s\", ", escape(c.getTeacher2())));
            sb.append(String.format("\"credit\": %.2f, ", c.getCredit()));
            sb.append(String.format("\"grade\": \"%s\"", escape(c.getGrade())));
            sb.append("}");
            if (i < list.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }
    
    public void editCourse(Course course) {
        if (course == null) return;
        course_title.setText(course.getTitle());
        course_code.setText(course.getCode());
        course_teacher1.setText(course.getTeacher1());
        course_teacher2.setText(course.getTeacher2());
        course_credit.setValue(course.getCredit());
        course_grade.setValue(course.getGrade());
        editingCourseCode = course.getCode();
        addButton.setText("Save Changes");
    }

    
    public void shutdown() {
        executor.shutdownNow();
    }
    
}