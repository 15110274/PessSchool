package com.example.preschool;

import java.io.Serializable;

public class User implements Serializable {
    public String fullname,username,idclass,classname,birthday,parentof,profileimage,userid,email,role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getIdclass() {
        return idclass;
    }

    public void setIdclass(String idclass) {
        this.idclass = idclass;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public User(String fullname, String username, String idclass, String classname, String birthday, String parentof, String profileimage, String userid, UserState userState) {
        this.fullname = fullname;
        this.username = username;
        this.idclass = idclass;
        this.classname = classname;
        this.birthday = birthday;
        this.parentof = parentof;
        this.profileimage = profileimage;
        this.userid = userid;
        this.userState = userState;
    }

    public User() {
    }
}
