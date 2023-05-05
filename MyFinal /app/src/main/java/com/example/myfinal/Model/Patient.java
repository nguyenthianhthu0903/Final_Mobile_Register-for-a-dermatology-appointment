package com.example.myfinal.Model;

public class Patient {

    private String name, phone, avt, id_patient;
    private int medicine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public int getMedicine() {
        return medicine;
    }

    public void setMedicine(int medicine) {
        this.medicine = medicine;
    }

    public int getChat() {
        return chat;
    }

    public void setChat(int chat) {
        this.chat = chat;
    }

    private int chat;

    public Patient() {}

    public Patient(String name, String avt) {
        this.name = name;
        this.avt = avt;
    }

    public Patient(String name, String avt, int medicine, int chat) {
        this.name = name;
        this.avt = avt;
        this.medicine = medicine;
        this.chat = chat;
    }

    public Patient(String name, String avt, int medicine, int chat, String phone) {
        this.name = name;
        this.avt = avt;
        this.medicine = medicine;
        this.chat = chat;
        this.phone = phone;
    }

    public Patient(String name, String avt, int medicine, int chat, String phone, String id_patient) {
        this.name = name;
        this.avt = avt;
        this.medicine = medicine;
        this.chat = chat;
        this.phone = phone;
        this.id_patient = id_patient;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId_patient() {
        return id_patient;
    }

    public void setId_patient(String id_patient) {
        this.id_patient = id_patient;
    }
}
