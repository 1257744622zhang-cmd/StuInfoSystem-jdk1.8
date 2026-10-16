package com.lc.demo.bean;

/**
 * @ClassName Subject
 * @Deacription 课程实体类
 * @Author daier
 * @Date 2021/1/6
 * @Version 1.0
 **/
public class Subject {

    private String subId;        // 课程编号
    private String subName;      // 课程名称
    private String subTerm;      // 学期（格式：22-2023-1）

    // 无参构造函数
    public Subject() {
    }

    // 全参构造函数
    public Subject(String subId, String subName, String subTerm) {
        this.subId = subId;
        this.subName = subName;
        this.subTerm = subTerm;
    }

    // Getter 和 Setter 方法
    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getSubTerm() {
        return subTerm;
    }

    public void setSubTerm(String subTerm) {
        this.subTerm = subTerm;
    }

    // toString 方法
    @Override
    public String toString() {
        return "Subject{" +
                "subId='" + subId + '\'' +
                ", subName='" + subName + '\'' +
                ", subTerm='" + subTerm + '\'' +
                '}';
    }

    // equals 和 hashCode 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        return subId != null ? subId.equals(subject.subId) : subject.subId == null;
    }

    @Override
    public int hashCode() {
        return subId != null ? subId.hashCode() : 0;
    }
}