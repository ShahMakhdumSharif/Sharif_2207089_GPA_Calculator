package com.example;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Course {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty code = new SimpleStringProperty();
    private final StringProperty teacher1 = new SimpleStringProperty();
    private final StringProperty teacher2 = new SimpleStringProperty();
    private final DoubleProperty credit = new SimpleDoubleProperty();
    private final StringProperty grade = new SimpleStringProperty();

    public Course() {
    }

    public Course(String title, String code, String teacher1, String teacher2, double credit, String grade) {
        this.title.set(title);
        this.code.set(code);
        this.teacher1.set(teacher1);
        this.teacher2.set(teacher2);
        this.credit.set(credit);
        this.grade.set(grade);
    }

    public StringProperty titleProperty() { return title; }
    public StringProperty codeProperty() { return code; }
    public StringProperty teacher1Property() { return teacher1; }
    public StringProperty teacher2Property() { return teacher2; }
    public DoubleProperty creditProperty() { return credit; }
    public StringProperty gradeProperty() { return grade; }

    public String getTitle() { return title.get(); }
    public String getCode() { return code.get(); }
    public String getTeacher1() { return teacher1.get(); }
    public String getTeacher2() { return teacher2.get(); }
    public double getCredit() { return credit.get(); }
    public String getGrade() { return grade.get(); }

    public void setTitle(String v) { title.set(v); }
    public void setCode(String v) { code.set(v); }
    public void setTeacher1(String v) { teacher1.set(v); }
    public void setTeacher2(String v) { teacher2.set(v); }
    public void setCredit(double v) { credit.set(v); }
    public void setGrade(String v) { grade.set(v); }
}