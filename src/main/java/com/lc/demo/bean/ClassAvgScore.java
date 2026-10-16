package com.lc.demo.bean;

public class ClassAvgScore {
    private String className;
    private String subName;
    private double avgScore;

    public ClassAvgScore() {
    }

    public ClassAvgScore(String className, String subName, double avgScore) {
        this.className = className;
        this.subName = subName;
        this.avgScore = avgScore;
    }

    // Getters and Setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    @Override
    public String toString() {
        return "ClassAvgScore{" +
                "className='" + className + '\'' +
                ", subName='" + subName + '\'' +
                ", avgScore=" + avgScore +
                '}';
    }
}