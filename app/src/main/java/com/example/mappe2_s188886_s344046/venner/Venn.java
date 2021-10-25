package com.example.mappe2_s188886_s344046.venner;

import androidx.annotation.NonNull;

public class Venn {
    private long _id;
    private String navn;
    private String telefon;

    public Venn() {}

    public Venn(String navn, String telefon) {
        this.navn = navn;
        this.telefon = telefon;
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

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    @NonNull
    @Override
    public String toString() {
        return this.navn + " - " + this.telefon;
    }
}
