package com.students.easyattendance;

public class Student {
    private String studentName;
    private String rollNumber;
    private String email;

    public Student() {
        // Default constructor required for Firebase
    }

    public Student(String studentName, String rollNumber, String email) {
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.email = email;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getEmail() {
        return email;
    }
}
