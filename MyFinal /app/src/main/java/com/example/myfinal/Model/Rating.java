package com.example.myfinal.Model;


import com.google.firebase.Timestamp;

public class Rating {
    private String id_doctor;
    private String id_patient;
    private String namePatient;
    private String nameDoctor;
    private String status;

    public Rating(String id_doctor, String id_patient, String namePatient, String nameDoctor, String status, String comment, int star, Timestamp date) {
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
        this.namePatient = namePatient;
        this.nameDoctor = nameDoctor;
        this.status = status;
        this.comment = comment;
        this.star = star;
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;
    private float star;
    private Timestamp date;

    public Rating(String id_doctor, String id_patient, String namePatient, String nameDoctor,
                  String status, String comment, float star, Timestamp date,
                  Timestamp date_completed) {
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
        this.namePatient = namePatient;
        this.nameDoctor = nameDoctor;
        this.status = status;
        this.comment = comment;
        this.star = star;
        this.date = date;
        this.date_completed = date_completed;
    }

    public Timestamp getDate_completed() {
        return date_completed;
    }

    public void setDate_completed(Timestamp date_completed) {
        this.date_completed = date_completed;
    }

    private Timestamp date_completed;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Rating() {}
    public Rating(String id_doctor, String id_patient, String namePatient, String nameDoctor, String status, int star, Timestamp date) {
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
        this.namePatient = namePatient;
        this.nameDoctor = nameDoctor;
        this.status = status;
        this.star = star;
        this.date = date;
    }
}
