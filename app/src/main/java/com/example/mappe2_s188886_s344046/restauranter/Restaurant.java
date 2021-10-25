package com.example.mappe2_s188886_s344046.restauranter;

import androidx.annotation.NonNull;

public class Restaurant {
    private long _id;
    private String navn;
    private String adresse;
    private String telefon;
    private String type;

    public Restaurant() {}

    public Restaurant(String navn, String adresse, String telefon, String type) {
        this.navn = navn;
        this. adresse = adresse;
        this.telefon = telefon;
        this.type = type;
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "Navn: " + this.navn + "\nAdresse: " + this.adresse + "\nTelefon: " + this.telefon + "\nType: " + this.type;
    }

}
