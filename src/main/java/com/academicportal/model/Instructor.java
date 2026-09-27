package com.academicportal.model;

public class Instructor {

    private int instructorID;
    private String firstName;
    private String lastName;
    private String email;
    private String expertise;

    public Instructor(int instructorID, String firstName, String lastName,
                      String email, String expertise) {
        this.instructorID = instructorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.expertise = expertise;
    }

    public int getInstructorID() {
        return instructorID;
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

    public String getExpertise() {
        return expertise;
    }
}