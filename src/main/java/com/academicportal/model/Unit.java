package com.academicportal.model;

public class Unit {

    private int unitID;
    private String unitTitle;
    private int estimatedCompletionTime;
    private int sequenceOrder;
    private int programID;

    private String programTitle;

    public Unit(int unitID, String unitTitle, int estimatedCompletionTime,
                int sequenceOrder, int programID) {
        this(unitID, unitTitle, estimatedCompletionTime, sequenceOrder, programID, null);
    }

    public Unit(int unitID, String unitTitle, int estimatedCompletionTime,
                int sequenceOrder, int programID, String programTitle) {
        this.unitID = unitID;
        this.unitTitle = unitTitle;
        this.estimatedCompletionTime = estimatedCompletionTime;
        this.sequenceOrder = sequenceOrder;
        this.programID = programID;
        this.programTitle = programTitle;
    }

    public int getUnitID() {
        return unitID;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public int getEstimatedCompletionTime() {
        return estimatedCompletionTime;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public int getProgramID() {
        return programID;
    }

    public String getProgramTitle() {
        return programTitle;
    }
}