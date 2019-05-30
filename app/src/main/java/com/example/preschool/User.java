package com.example.preschool;

public class User {
    String address,dod,fullname,gender,idClass,profileimage,status,username;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDod() {
        return dod;
    }

    public void setDod(String dod) {
        this.dod = dod;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdClass() {
        return idClass;
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public com.example.preschool.userState getUserState() {
        return userState;
    }

    public void setUserState(com.example.preschool.userState userState) {
        this.userState = userState;
    }

    public User() {
    }

    userState userState;

    public User(String address, String dod, String fullname, String gender, String idClass, String profileimage, String status, String username, com.example.preschool.userState userState) {
        this.address = address;
        this.dod = dod;
        this.fullname = fullname;
        this.gender = gender;
        this.idClass = idClass;
        this.profileimage = profileimage;
        this.status = status;
        this.username = username;
        this.userState = userState;
    }
}
