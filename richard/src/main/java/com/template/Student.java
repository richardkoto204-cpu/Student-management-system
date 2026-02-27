package com.template;//package domain;

public class Student{
    private final String studentId;
    private final String fullName;
    private final String programme;
    private final String level;
    private final String gpa;
    private final String email;
    private final String phone;

    public Student(String studentId, String fullName, String programme, String level,
                   String gpa, String email, String phone) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.programme = programme;
        this.level = level;
        this.gpa = gpa;
        this.email = email;
        this.phone = phone;
    }

    // Getters required by TableView
    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getProgramme() { return programme; }
    public String getLevel() { return level; }
    public String getGpa() { return gpa; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
