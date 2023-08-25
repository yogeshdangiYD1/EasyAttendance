package com.students.easyattendance;


public class ClassData {
    private String className;
    private String batch;

    public ClassData(String className, String batch) {
        this.className = className;
        this.batch = batch;
    }

    public String getClassName() {
        return className;
    }

    public String getBatch() {
        return batch;
    }
}

