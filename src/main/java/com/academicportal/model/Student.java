package com.academicportal.model;

import java.time.LocalDateTime;

public class Student {

    private int studentID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDateTime registrationDate;

    public Student(int studentID, String firstName, String lastName,
                   String email, String phone, LocalDateTime registrationDate) {
        this.studentID = studentID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
    }

    public int getStudentID() {
        return studentID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
}