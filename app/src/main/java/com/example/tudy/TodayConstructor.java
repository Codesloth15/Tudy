package com.example.tudy;

public class TodayConstructor {
    String task;
    String date;
    String status,uid;


    public TodayConstructor(String task, String date, String status, String uid) {
        this.task = task;
        this.date = date;
        this.status = status;
        this.uid = uid;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
