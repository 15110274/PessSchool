package com.example.preschool;

public class Children {
    String birthday, parentof;

    public Children() {
    }

    public Children(String birthday, String parentof) {
        this.birthday = birthday;
        this.parentof = parentof;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getParentof() {
        return parentof;
    }

    public void setParentof(String parentof) {
        this.parentof = parentof;
    }
}
