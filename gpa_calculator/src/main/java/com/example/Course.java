package com.example;

public class Course {
    private String title;
    private String code;
    private String teacher1;
    private String teacher2;
    private double credit;
    private String grade;

    public Course(String title, String code, String teacher1, String teacher2, double credit, String grade) {
        this.title = title;
        this.code = code;
        this.teacher1 = teacher1;
        this.teacher2 = teacher2;
        this.credit = credit;
        this.grade = grade;
    }
    // Course Title
    public String getTitle() { return title; }

    // Course Code
    public String getCode() { return code; }

    // Course Teacher1
    public String getTeacher1() { return teacher1; }

    // Course Teacher2
    public String getTeacher2() { return teacher2; }

    // Course Credit
    public double getCredit() { return credit; }

    // Course Grade
    public String getGrade() { return grade; }
}