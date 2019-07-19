package com.example.preschool.Admin;

public class Class {
    String classname,teacher;

    public Class() {
    }

    public Class(String classname, String teacher) {
        this.classname = classname;
        this.teacher = teacher;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
