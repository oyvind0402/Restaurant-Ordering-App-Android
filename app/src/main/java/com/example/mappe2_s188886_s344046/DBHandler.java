package com.example.mappe2_s188886_s344046;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RestaurantBestilling";

    private static final String TABLENAME = "restaurant";
    private static final String KEY_ID = "_id";
    private static final String NAME = "navn";
    private static final String ADDRESS = "adresse";
    private static final String PHONE = "telefon";
    private static final String TYPE = "type";

    private static final String TABLENAME2 = "venn";
    private static final String KEY_ID2 = "_id";
    private static final String NAME2 = "navn";
    private static final String PHONE2 = "telefon";

    private static final String TABLENAME3 = "bestilling";
    private static final String KEY_ID3 = "_id";
    private static final String RESTAURANT = "restaurantid";
    private static final String DATO = "dato";
    private static final String TIDSPUNKT = "tidspunkt";
    //Venner vil bli lagret som en liste, men serialisert og deserialisert.
    //Kan ikke sette den som en foreign key pga dette.
    private static final String VENNER = "venner";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLENAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + ADDRESS + " TEXT, " + PHONE + " TEXT, " + TYPE + " TEXT);");
        db.execSQL("CREATE TABLE " + TABLENAME2 + " (" + KEY_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME2 + " TEXT, " + PHONE2 + " TEXT);");
        db.execSQL("CREATE TABLE " + TABLENAME3 + " (" + KEY_ID3 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RESTAURANT + " INTEGER, " + DATO + " TEXT, " + TIDSPUNKT + " TEXT, " + VENNER + " TEXT, FOREIGN KEY ('restaurantid') REFERENCES restaurant ('_id'));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int j) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME2 + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME3 + ";");
    }

    public void leggTilRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, restaurant.getNavn());
        values.put(ADDRESS, restaurant.getAdresse());
        values.put(PHONE, restaurant.getTelefon());
        values.put(TYPE, restaurant.getType());
        db.insert(TABLENAME, null, values);
        db.close();
    }

    public List<Restaurant> finnAlleRestauranter() {
        List<Restaurant> restaurantListe = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLENAME + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                Restaurant restaurant = new Restaurant();
                restaurant.set_id(cursor.getLong(0));
                restaurant.setNavn(cursor.getString(1));
                restaurant.setAdresse(cursor.getString(2));
                restaurant.setTelefon(cursor.getString(3));
                restaurant.setType(cursor.getString(4));
                restaurantListe.add(restaurant);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return restaurantListe;
    }

    public void slettRestaurant(Long _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLENAME, KEY_ID + " = ?", new String[]{Long.toString(_id)});
        db.close();
    }

    public int oppdaterRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, restaurant.getNavn());
        values.put(ADDRESS, restaurant.getAdresse());
        values.put(PHONE, restaurant.getTelefon());
        values.put(TYPE, restaurant.getType());
        int endret = db.update(TABLENAME, values, KEY_ID + " = ?", new String[]{String.valueOf(restaurant.get_id())});
        db.close();
        return endret;
    }

    public void leggTilVenn(Venn venn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME2, venn.getNavn());
        values.put(PHONE2, venn.getTelefon());
        db.insert(TABLENAME2, null, values);
        db.close();
    }

    public List<Venn> finnAlleVenner() {
        List<Venn> venneListe = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLENAME2 + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                Venn venn = new Venn();
                venn.set_id(cursor.getLong(0));
                venn.setNavn(cursor.getString(1));
                venn.setTelefon(cursor.getString(2));
                venneListe.add(venn);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return venneListe;
    }

    public void slettVenn(Long _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLENAME2, KEY_ID2 + " = ?", new String[]{Long.toString(_id)});
        db.close();
    }

    public int oppdaterVenn(Venn venn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME2, venn.getNavn());
        values.put(PHONE2, venn.getTelefon());
        int endret = db.update(TABLENAME2, values, KEY_ID2 + " = ?", new String[]{String.valueOf(venn.get_id())});
        db.close();
        return endret;
    }

    public void leggTilBestilling(Bestilling bestilling) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RESTAURANT, bestilling.getRestaurantid());
        values.put(DATO, bestilling.getDato());
        values.put(TIDSPUNKT, bestilling.getTidspunkt());
        values.put(VENNER, bestilling.getVenner());
    }

    public List<Bestilling> finnALleBestillinger() {
        List<Bestilling> bestillingsListe = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLENAME3 + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                Bestilling bestilling = new Bestilling();
                bestilling.set_id(cursor.getLong(0));
                bestilling.setRestaurantid(cursor.getLong(1));
                bestilling.setDato(cursor.getString(2));
                bestilling.setTidspunkt(cursor.getString(3));
                bestilling.setVenner(cursor.getString(4));
                bestillingsListe.add(bestilling);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return bestillingsListe;
    }
}
