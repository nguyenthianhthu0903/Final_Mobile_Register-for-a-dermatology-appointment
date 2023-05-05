package com.example.myfinal.Model;

import com.google.firebase.Timestamp;

public class Appointment {

    private String id_doctor, id_patient, nameDoctor, namePatient, status;
    private Timestamp date;

    public Appointment() {}

    public Appointment(String namePatient, String status) {
        this.namePatient = namePatient;
        this.status = status;
    }

    public Appointment(String id_doctor, String id_patient, String nameDoctor, String namePatient, String status, Timestamp date) {
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
        this.nameDoctor = nameDoctor;
        this.namePatient = namePatient;
        this.status = status;
        this.date = date;
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

    public String getNameDoctor() {
        return nameDoctor;
    }

    public void setNameDoctor(String nameDoctor) {
        this.nameDoctor = nameDoctor;
    }

    public String getNamePatient() {
        return namePatient;
    }

    public void setNamePatient(String namePatient) {
        this.namePatient = namePatient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
