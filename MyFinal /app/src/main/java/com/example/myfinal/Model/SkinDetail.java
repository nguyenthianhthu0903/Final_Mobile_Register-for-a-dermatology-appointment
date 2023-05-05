package com.example.myfinal.Model;

import java.sql.Timestamp;

public class SkinDetail {
    private String id_doctor, id_patient, nameDoctor, namePatient, phonePatient, conclusion,
            skinType, skinAge, skinTone;
    private boolean isCompleted;

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

    public String getPhonePatient() {
        return phonePatient;
    }

    public void setPhonePatient(String phonePatient) {
        this.phonePatient = phonePatient;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public String getSkinAge() {
        return skinAge;
    }

    public void setSkinAge(String skinAge) {
        this.skinAge = skinAge;
    }

    public String getSkinTone() {
        return skinTone;
    }

    public void setSkinTone(String skinTone) {
        this.skinTone = skinTone;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    private Timestamp date;
    private String wrinkles;
    private String sagging;

    public SkinDetail(String id_doctor, String id_patient, String nameDoctor, String namePatient, String phonePatient, String conclusion, String skinType, String skinAge, String skinTone, boolean isCompleted, Timestamp date, String wrinkles, String sagging, String pigmentation, String pores, String translucency, String redness, String hydration, String summary) {
        this.id_doctor = id_doctor;
        this.id_patient = id_patient;
        this.nameDoctor = nameDoctor;
        this.namePatient = namePatient;
        this.phonePatient = phonePatient;
        this.conclusion = conclusion;
        this.skinType = skinType;
        this.skinAge = skinAge;
        this.skinTone = skinTone;
        this.isCompleted = isCompleted;
        this.date = date;
        this.wrinkles = wrinkles;
        this.sagging = sagging;
        this.pigmentation = pigmentation;
        this.pores = pores;
        this.translucency = translucency;
        this.redness = redness;
        this.hydration = hydration;
        this.summary = summary;
    }

    public String getWrinkles() {
        return wrinkles;
    }

    public void setWrinkles(String wrinkles) {
        this.wrinkles = wrinkles;
    }

    public String getSagging() {
        return sagging;
    }

    public void setSagging(String sagging) {
        this.sagging = sagging;
    }

    public String getPigmentation() {
        return pigmentation;
    }

    public void setPigmentation(String pigmentation) {
        this.pigmentation = pigmentation;
    }

    public String getPores() {
        return pores;
    }

    public void setPores(String pores) {
        this.pores = pores;
    }

    public String getTranslucency() {
        return translucency;
    }

    public void setTranslucency(String translucency) {
        this.translucency = translucency;
    }

    public String getRedness() {
        return redness;
    }

    public void setRedness(String redness) {
        this.redness = redness;
    }

    public String getHydration() {
        return hydration;
    }

    public void setHydration(String hydration) {
        this.hydration = hydration;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    private String pigmentation;
    private String pores;
    private String translucency;
    private String redness;
    private String hydration;
    private String summary;

    public SkinDetail() {}

}
