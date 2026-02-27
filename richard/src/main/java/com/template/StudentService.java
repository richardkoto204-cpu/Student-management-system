package com.template;

import java.security.PublicKey;
import java.util.List;

public class StudentService {

    public void validateStudent(Student student) {

        // com.template.Student ID
        if (student.getStudentId() == null || student.getStudentId().isEmpty()) {
            throw new IllegalArgumentException("com.template.Student ID is required");
        }
        if (!student.getStudentId().matches("[A-Za-z0-9]{4,9}")) {
            throw new IllegalArgumentException("com.template.Student ID must be 4–20 letters or digits");
        }

        // Full name
        if (student.getFullName() == null || student.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (!student.getFullName().matches("[A-Za-z ]{2,60}")) {
            throw new IllegalArgumentException("Full name must not contain digits");
        }

        // Programme
        if (student.getProgramme() == null || student.getProgramme().isEmpty()) {
            throw new IllegalArgumentException("Programme is required");
        }

        // Level
        String level = student.getLevel();
        if (!level.matches("100|200|300|400")) {
            throw new IllegalArgumentException("Level must be 100–400");
        }

        // GPA
        try {
            double gpa = Double.parseDouble(student.getGpa());
            if (gpa < 0.0 || gpa > 5.0) {
                throw new IllegalArgumentException("GPA must be between 0.0 and 5.0");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("GPA must be a number");
        }

        // Email
        if (!student.getEmail().contains("@") || !student.getEmail().contains(".")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // Phone
        if (!student.getPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone must be 10 digits");
        }
    }

    public List<Student> getAtRiskStudents(double threshold) {
        return StudentDAO.findStudentsBelowGpa(threshold);
    }

    public boolean addStudent(Student student){
        if(StudentDAO.studentExists(student.getStudentId())){
            AppLogger.log("Duplicate student skipped:" + student.getStudentId());
            return false;
        }
        StudentDAO.insert(student);
        AppLogger.log("Student added:" + student.getStudentId());
        return true;
    }

}
