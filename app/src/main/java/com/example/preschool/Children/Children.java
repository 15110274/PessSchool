package com.example.preschool.Children;

public class Children {
    String birthday, name;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Children(String birthday, String name) {
        this.birthday = birthday;
        this.name = name;
    }

    public Children() {
    }
}
