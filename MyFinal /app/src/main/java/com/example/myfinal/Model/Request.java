package com.example.myfinal.Model;

import com.google.firebase.Timestamp;

public class Request {

    private String namePatient, nameDoctor, phonePatient, id_doctor, id_patient, status;
    private Timestamp date;
    private int avt;

    public Request() {}


    public Request(String namePatient, String nameDoctor, String phonePatient, String id_doctor, String id_patient, Timestamp date) {
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
        this.namePatient = namePatient;
        this.nameDoctor = nameDoctor;
        this.phonePatient = phonePatient;
        this.date = date;
    }

    public int getAvt() {
        return avt;
    }

    public void setAvt(int avt) {
        this.avt = avt;
    }

    public String getId_doctor() {
        return id_doctor;
    }

    public void setId_doctor(String id_doctor) {
        this.id_doctor = id_doctor;
    }

    public String getId_patient() {
        return id_patient;
    }

    public void setId_patient(String id_patient) {
        this.id_patient = id_patient;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamePatient() {
        return namePatient;
    }

    public void setNamePatient(String namePatient) {
        this.namePatient = namePatient;
    }

    public String getNameDoctor() {
        return nameDoctor;
    }

    public void setNameDoctor(String nameDoctor) {
        this.nameDoctor = nameDoctor;
    }

    public String getPhonePatient() {
        return phonePatient;
    }

    public void setPhonePatient(String phonePatient) {
        this.phonePatient = phonePatient;
    }
}

