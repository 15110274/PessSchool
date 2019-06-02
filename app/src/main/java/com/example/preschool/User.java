package com.example.preschool;

import java.io.Serializable;

public class User implements Serializable {
    public String fullname,username,idClass,classname,birthday,parentof,profileimage;
    public UserState userState;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdClass() {
        return idClass;
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
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

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public User(String fullname, String username, String idClass, String classname, String birthday, String parentof, String profileimage, UserState userState) {
        this.fullname = fullname;
        this.username = username;
        this.idClass = idClass;
        this.classname = classname;
        this.birthday = birthday;
        this.parentof = parentof;
        this.profileimage = profileimage;
        this.userState = userState;
    }

    public User() {
    }
}
