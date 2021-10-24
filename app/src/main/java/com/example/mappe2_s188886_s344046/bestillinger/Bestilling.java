package com.example.mappe2_s188886_s344046.bestillinger;

import com.example.mappe2_s188886_s344046.venner.Venn;

import java.util.List;

public class Bestilling {
    private long _id;
    private long restaurantid;
    private String dato;
    private String tidspunkt;
    private List<Venn> venner;

    public Bestilling() {}

    public Bestilling(long restaurantid, String dato, String tidspunkt, List<Venn> venner) {
        this.restaurantid = restaurantid;
        this.dato = dato;
        this.tidspunkt = tidspunkt;
        this.venner = venner;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getRestaurantid() {
        return restaurantid;
    }

    public void setRestaurantid(long restaurantid) {
        this.restaurantid = restaurantid;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getTidspunkt() {
        return tidspunkt;
    }

    public void setTidspunkt(String tidspunkt) {
        this.tidspunkt = tidspunkt;
    }

    public List<Venn> getVenner() {
        return venner;
    }

    public void setVenner(List<Venn> venner) {
        this.venner = venner;
    }
}
