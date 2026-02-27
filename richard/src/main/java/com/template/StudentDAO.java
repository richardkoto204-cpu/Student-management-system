package com.template;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String DB_URL = "jdbc:sqlite:students.db";

    // Connect to SQLite database
    private static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

    // Create table if it does not exist
    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    studentId TEXT PRIMARY KEY,
                    fullName TEXT,
                    programme TEXT,
                    level TEXT,
                    gpa TEXT,
                    email TEXT,
                    phone TEXT
                );
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    // Insert student
    public static void insert(Student student) {
        String sql = """
                INSERT INTO students(studentId, fullName, programme, level, gpa, email, phone)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getProgramme());
            pstmt.setString(4, student.getLevel());
            pstmt.setString(5, student.getGpa());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhone());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }

    // Retrieve all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("studentId"),
                        rs.getString("fullName"),
                        rs.getString("programme"),
                        rs.getString("level"),
                        rs.getString("gpa"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                students.add(student);
            }

        } catch (SQLException e) {
            System.out.println("Read failed: " + e.getMessage());
        }

        return students;
    }

    // Update student
    public void update(Student student) {
        String sql = """
                UPDATE students
                SET fullName = ?, programme = ?, level = ?, gpa = ?, email = ?, phone = ?
                WHERE studentId = ?;
                """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getProgramme());
            pstmt.setString(3, student.getLevel());
            pstmt.setString(4, student.getGpa());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getStudentId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // Delete student
    public void delete(String studentId) {
        String sql = "DELETE FROM students WHERE studentId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    public static boolean studentExists(String studentId) {
        String sql = "SELECT 1 FROM students WHERE studentId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }

    public static List<Student> findStudentsBelowGpa(double threshold) {
        List<Student> result = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE gpa < ?";

        try (Connection conn =   connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, threshold);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("studentId"),
                        rs.getString("fullName"),
                        rs.getString("programme"),
                        rs.getString("level"),
                        rs.getString("gpa"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                result.add(student);
            }
        } catch (SQLException e) {
            System.out.println("Error loading At Risk students: " + e.getMessage());
        }

        return result;
    }

}
