package com.example.tudy;

public class TodayConstructor {
    String task;
    String date;
    String status;

    public TodayConstructor(String task, String date, String status) {
        this.task = task;
        this.date = date;
        this.status = status;
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
