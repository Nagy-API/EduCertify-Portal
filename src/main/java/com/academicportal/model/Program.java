package com.academicportal.model;

public class Program {

    private int programID;
    private String programTitle;
    private String difficultyLevel;
    private double registrationFee;
    private String status;
    private int instructorID;
    private int categoryID;

    private String instructorName;
    private String categoryName;

    public Program(int programID, String programTitle, String difficultyLevel,
                   double registrationFee, String status, int instructorID, int categoryID) {
        this(programID, programTitle, difficultyLevel, registrationFee, status,
                instructorID, categoryID, null, null);
    }

    public Program(int programID, String programTitle, String difficultyLevel,
                   double registrationFee, String status, int instructorID, int categoryID,
                   String instructorName, String categoryName) {
        this.programID = programID;
        this.programTitle = programTitle;
        this.difficultyLevel = difficultyLevel;
        this.registrationFee = registrationFee;
        this.status = status;
        this.instructorID = instructorID;
        this.categoryID = categoryID;
        this.instructorName = instructorName;
        this.categoryName = categoryName;
    }

    public int getProgramID() {
        return programID;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public double getRegistrationFee() {
        return registrationFee;
    }

    public String getStatus() {
        return status;
    }

    public int getInstructorID() {
        return instructorID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}