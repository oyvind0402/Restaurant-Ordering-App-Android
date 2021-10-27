package com.example.mappe2_s188886_s344046.utils;

import java.util.Calendar;

public class Formatters {
    public static void formatDate(Calendar cal, int year, int month, int day, Integer hour, Integer min){
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        if (hour != null && min != null) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
        }
    }
}
