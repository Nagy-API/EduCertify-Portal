package com.academicportal.model;

import java.time.LocalDateTime;

public class Enrollment {

    private int studentID;
    private int programID;
    private LocalDateTime signupDate;
    private int progressPercentage;
    private String completionStatus;

    private String studentName;
    private String programTitle;

    public Enrollment(int studentID, int programID, LocalDateTime signupDate,
                      int progressPercentage, String completionStatus,
                      String studentName, String programTitle) {
        this.studentID = studentID;
        this.programID = programID;
        this.signupDate = signupDate;
        this.progressPercentage = progressPercentage;
        this.completionStatus = completionStatus;
        this.studentName = studentName;
        this.programTitle = programTitle;
    }

    public int getStudentID() {
        return studentID;
    }

    public int getProgramID() {
        return programID;
    }

    public LocalDateTime getSignupDate() {
        return signupDate;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getProgramTitle() {
        return programTitle;
    }
}