package com.academicportal.model;

import java.time.LocalDateTime;

public class UnitProgress {

    private int studentID;
    private int unitID;
    private String unitStatus;
    private LocalDateTime completionDate;

    private String studentName;
    private String unitTitle;
    private String programTitle;

    public UnitProgress(int studentID, int unitID, String unitStatus,
                        LocalDateTime completionDate, String studentName,
                        String unitTitle, String programTitle) {
        this.studentID = studentID;
        this.unitID = unitID;
        this.unitStatus = unitStatus;
        this.completionDate = completionDate;
        this.studentName = studentName;
        this.unitTitle = unitTitle;
        this.programTitle = programTitle;
    }

    public int getStudentID() {
        return studentID;
    }

    public int getUnitID() {
        return unitID;
    }

    public String getUnitStatus() {
        return unitStatus;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public String getProgramTitle() {
        return programTitle;
    }
}