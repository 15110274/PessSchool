package com.example.preschool;

public class FindFriends {
    public String profileimage,fullname, address;

    public FindFriends() {
    }

    public FindFriends(String profileimage, String fullname, String address) {
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.address = address;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
