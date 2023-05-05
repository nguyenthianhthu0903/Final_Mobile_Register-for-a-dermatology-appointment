package com.example.myfinal.Model;

public class Doctor {

    private String name, about, email;
    private float rating;
    private int avt;

    public Doctor() {}

    public Doctor(String name, float rating, String email, int avt, String about) {
        this.name = name;
        this.rating = rating;
        this.email = email;
        this.avt = avt;
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvt() {
        return avt;
    }

    public void setAvt(int avt) {
        this.avt = avt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
