package com.app;

public class Doubt {
    private String doubtId;
    private String userId;
    private String department;
    private String doubtText;
    private String audioPath;

    // Constructors
    public Doubt() {
    }

    public Doubt(String doubtId, String userId, String department, String doubtText, String audioPath) {
        this.doubtId = doubtId;
        this.userId = userId;
        this.department = department;
        this.doubtText = doubtText;
        this.audioPath = audioPath;
    }

    // Getters and Setters
    public String getDoubtId() {
        return doubtId;
    }

    public void setDoubtId(String doubtId) {
        this.doubtId = doubtId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDoubtText() {
        return doubtText;
    }

    public void setDoubtText(String doubtText) {
        this.doubtText = doubtText;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}
