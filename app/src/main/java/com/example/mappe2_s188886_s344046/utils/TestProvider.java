package com.example.mappe2_s188886_s344046.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TestProvider extends ContentProvider{
    private static final int alleRestauranter = 2;
    private static final int enRestaurant = 1;
    private static final int alleVenner = 100;
    private static final int enVenn = 101;
    public final static String PROVIDER = "com.example.mappe2_s188886_s344046";
    DBHandler dbHandler;
    SQLiteDatabase db;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "restaurant", alleRestauranter);
        uriMatcher.addURI(PROVIDER, "restaurant/#", enRestaurant);
        uriMatcher.addURI(PROVIDER, "venn", alleVenner);
        uriMatcher.addURI(PROVIDER, "venn/#", enVenn);
    }

    @Override
    public boolean onCreate() {
        dbHandler = new DBHandler(getContext());
        db = dbHandler.getWritableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case alleRestauranter:
                return "vnd.android.cursor.dir/vnd.example.restaurant";
            case enRestaurant:
                return "vnd.android.cursor.item/vnd.example.restaurant";
            case alleVenner:
                return "vnd.android.cursor.dir/vnd.example.venn";
            case enVenn:
                return "vnd.android.cursor.item/vnd.example.venn";
            default:
                throw new IllegalArgumentException("Ugyldig URI " + uri);
        }
    }

    @Override
    public Uri insert (Uri uri, ContentValues values) {
        if(uriMatcher.match(uri) == alleRestauranter) {
            db.insert("restaurant", null, values);
            Cursor cursor = db.query("restaurant", null, null, null, null, null, null);
            cursor.moveToLast();
            long _id = cursor.getLong(0);
            getContext().getContentResolver().notifyChange(uri, null);
            cursor.close();
            return ContentUris.withAppendedId(uri, _id);
        } else {
            db.insert("venn", null, values);
            Cursor cursor = db.query("venn", null, null, null, null, null, null);
            cursor.moveToLast();
            long _id = cursor.getLong(0);
            getContext().getContentResolver().notifyChange(uri, null);
            cursor.close();
            return ContentUris.withAppendedId(uri, _id);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        if(uriMatcher.match(uri) == enRestaurant) {
            cursor = db.query("restaurant", projection, "_id" + " = " + uri.getPathSegments().get(1), selectionArgs, null, null, sortOrder);
        } else if (uriMatcher.match(uri) == alleRestauranter) {
            cursor = db.query("restaurant", null, null, null, null, null, null);
        } else if(uriMatcher.match(uri) == enVenn) {
            cursor = db.query("venn", projection, "_id" + " = " + uri.getPathSegments().get(1), selectionArgs, null, null, sortOrder);
        } else {
            cursor = db.query("venn", null, null, null, null, null, null);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(uriMatcher.match(uri) == enRestaurant) {
            db.update("restaurant", values, "_id" + " = " + uri.getPathSegments().get(1), null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        if(uriMatcher.match(uri) == alleRestauranter) {
            db.update("restaurant", null, null, null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 2;
        }
        if(uriMatcher.match(uri) == enVenn) {
            db.update("venn", values, "_id" + " = " + uri.getPathSegments().get(1), null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        if(uriMatcher.match(uri) == alleVenner) {
            db.update("venn", null, null, null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 2;
        }
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if(uriMatcher.match(uri) == enRestaurant) {
            db.delete("restaurant", "_id" + " = " + uri.getPathSegments().get(1), selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        if(uriMatcher.match(uri) == alleRestauranter) {
            db.delete("restaurant", null, null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 2;
        }
        if(uriMatcher.match(uri) == enVenn) {
            db.delete("venn", "_id" + " = " + uri.getPathSegments().get(1), selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        if(uriMatcher.match(uri) == alleVenner) {
            db.delete("venn", null, null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 2;
        }
        return 0;
    }
}
