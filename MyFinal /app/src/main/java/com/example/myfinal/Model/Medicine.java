package com.example.myfinal.Model;


import com.google.firebase.Timestamp;

public class Medicine {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public Medicine(String name, String dosage) {
        this.name = name;
        this.dosage = dosage;
    }

    private String name;

    public Medicine(String name, Timestamp date, String nameDoctor, String dosage, String id_doctor, String id_patient) {
        this.name = name;
        this.date = date;
        this.nameDoctor = nameDoctor;
        this.dosage = dosage;
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    private Timestamp date;

    public String getNameDoctor() {
        return nameDoctor;
    }

    public void setNameDoctor(String nameDoctor) {
        this.nameDoctor = nameDoctor;
    }

    private String nameDoctor;
    private String dosage;

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

    private String id_doctor;

    public Medicine(String name, String dosage, String id_doctor, String id_patient) {
        this.name = name;
        this.dosage = dosage;
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
    }

    public Medicine() {

    }

    private String id_patient;
}
