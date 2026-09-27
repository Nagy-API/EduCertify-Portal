package com.academicportal.model;

import java.time.LocalDateTime;

public class Credential {

    private int credentialID;
    private String verificationCode;
    private LocalDateTime grantedDate;
    private int studentID;
    private int programID;
    private String studentName;
    private String programTitle;

    public Credential(int credentialID, String verificationCode, LocalDateTime grantedDate,
                      int studentID, int programID, String studentName, String programTitle) {
        this.credentialID = credentialID;
        this.verificationCode = verificationCode;
        this.grantedDate = grantedDate;
        this.studentID = studentID;
        this.programID = programID;
        this.studentName = studentName;
        this.programTitle = programTitle;
    }

    public int getCredentialID() {
        return credentialID;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public LocalDateTime getGrantedDate() {
        return grantedDate;
    }

    public int getStudentID() {
        return studentID;
    }

    public int getProgramID() {
        return programID;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getProgramTitle() {
        return programTitle;
    }
}