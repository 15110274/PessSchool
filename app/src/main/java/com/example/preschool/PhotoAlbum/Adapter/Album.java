package com.example.preschool.PhotoAlbum.Adapter;


import java.util.ArrayList;

public class Album {


    private String name;
    private ArrayList<String> imageUrlList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(ArrayList<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public Album() {
    }

    public Album(String name, ArrayList<String> imageUrlList) {
        this.name = name;
        this.imageUrlList = imageUrlList;
    }
}

