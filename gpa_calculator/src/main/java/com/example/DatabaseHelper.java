package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper {
    private static final String DB_PATH = "gpa_calculator.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;
    private static DatabaseHelper instance;

    private DatabaseHelper() {
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) instance = new DatabaseHelper();
        return instance;
    }

    public void initDB() throws SQLException {
        try (Connection c = DriverManager.getConnection(URL)) {
            try (Statement s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS courses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT, " +
                        "code TEXT UNIQUE, " +
                        "teacher1 TEXT, " +
                        "teacher2 TEXT, " +
                        "credit REAL, " +
                        "grade TEXT" +
                        ")");
            }
        }
    }

    public List<Course> fetchAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL)) {
            try (PreparedStatement ps = c.prepareStatement("SELECT title,code,teacher1,teacher2,credit,grade FROM courses ORDER BY id")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Course cobj = new Course(
                                rs.getString("title"),
                                rs.getString("code"),
                                rs.getString("teacher1"),
                                rs.getString("teacher2"),
                                rs.getDouble("credit"),
                                rs.getString("grade")
                        );
                        list.add(cobj);
                    }
                }
            }
        }
        return list;
    }

    public boolean insertCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses(title,code,teacher1,teacher2,credit,grade) VALUES(?,?,?,?,?,?)";
        try (Connection c = DriverManager.getConnection(URL)) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, course.getTitle());
                ps.setString(2, course.getCode());
                ps.setString(3, course.getTeacher1());
                ps.setString(4, course.getTeacher2());
                ps.setDouble(5, course.getCredit());
                ps.setString(6, course.getGrade());
                ps.executeUpdate();
                return true;
            }
        }
    }

    public boolean updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET title=?, teacher1=?, teacher2=?, credit=?, grade=? WHERE code=?";
        try (Connection c = DriverManager.getConnection(URL)) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, course.getTitle());
                ps.setString(2, course.getTeacher1());
                ps.setString(3, course.getTeacher2());
                ps.setDouble(4, course.getCredit());
                ps.setString(5, course.getGrade());
                ps.setString(6, course.getCode());
                int updated = ps.executeUpdate();
                return updated > 0;
            }
        }
    }

    public boolean deleteCourseByCode(String code) throws SQLException {
        String sql = "DELETE FROM courses WHERE code=?";
        try (Connection c = DriverManager.getConnection(URL)) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, code);
                int deleted = ps.executeUpdate();
                return deleted > 0;
            }
        }
    }
}
