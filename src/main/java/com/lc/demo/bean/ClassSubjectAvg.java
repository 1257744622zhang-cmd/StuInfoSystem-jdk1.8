package com.lc.demo.bean;

public class ClassSubjectAvg {
    private String className;
    private String subName;
    private double avgScore;

    // 构造方法、getter和setter
    public ClassSubjectAvg() {}

    public ClassSubjectAvg(String className, String subName, double avgScore) {
        this.className = className;
        this.subName = subName;
        this.avgScore = avgScore;
    }

    // getter和setter方法
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSubName() { return subName; }
    public void setSubName(String subName) { this.subName = subName; }

    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
}