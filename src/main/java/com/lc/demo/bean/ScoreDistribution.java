package com.lc.demo.bean;

import java.util.List;

public class ScoreDistribution {
    private String subName;
    private double avgScore;
    private double stdDev;
    private int maxScore;
    private int minScore;
    private int totalCount;
    private List<ScoreBucket> scoreBuckets;

    public ScoreDistribution() {
    }

    // Getters and Setters
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

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<ScoreBucket> getScoreBuckets() {
        return scoreBuckets;
    }

    public void setScoreBuckets(List<ScoreBucket> scoreBuckets) {
        this.scoreBuckets = scoreBuckets;
    }
}