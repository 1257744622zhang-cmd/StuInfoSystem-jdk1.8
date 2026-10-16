package com.lc.demo.bean;

public class ScoreBucket {
    private String range;
    private int count;
    private double percentage;

    public ScoreBucket() {
    }

    public ScoreBucket(String range, int count, double percentage) {
        this.range = range;
        this.count = count;
        this.percentage = percentage;
    }

    // Getters and Setters
    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}