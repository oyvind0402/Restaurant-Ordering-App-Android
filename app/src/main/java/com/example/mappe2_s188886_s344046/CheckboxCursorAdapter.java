package com.example.mappe2_s188886_s344046;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//Klasse basert på: https://stackoverflow.com/a/47540946
public class CheckboxCursorAdapter extends CursorAdapter {
    private final Cursor cursor;
    private final String[] columns;
    private final int[] views;
    private final int layout;
    private boolean[] checkBoxChecked;
    private final int checkBoxView;

    public CheckboxCursorAdapter(Context context, int layout, Cursor cursor, String[] columns, int[] views, int checkBoxView) {
        super(context, cursor, false);
        this.layout = layout;
        this.cursor = cursor;
        this.columns = columns;
        this.views = views;
        this.checkBoxView = checkBoxView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        checkBoxChecked = new boolean[cursor.getCount()];
        return LayoutInflater.from(context).inflate(layout, parent, false);
    }

    @SuppressLint("Range")
    @Override
    public void bindView(View view, Context context, Cursor csr) {
        for(int i = 0; i < columns.length; i++) {
            ((TextView) view.findViewById(views[i])).setText(csr.getString(csr.getColumnIndex(columns[i])));
        }

        CheckBox checkBox = (CheckBox) view.findViewById(checkBoxView);
        checkBox.setChecked(checkBoxChecked[cursor.getPosition()]);
        checkBox.setTag(new Long(cursor.getLong(cursor.getColumnIndex("_id"))));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            final int position = cursor.getPosition();
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
                checkBoxChecked[position] = ((CheckBox) compoundButton).isChecked();
                int restore_cursor_position = cursor.getPosition();
                cursor.moveToPosition(position);
                cursor.moveToPosition(restore_cursor_position);
            }
        });
    }

    @SuppressLint("Range")
    public long[] getCheckedVennIdList() {
        ArrayList<Long> idListe = new ArrayList<>();
        int position = cursor.getPosition();
        for(int i = 0; i < checkBoxChecked.length; i++) {
            if(checkBoxChecked[i]) {
                cursor.moveToPosition(i);
                idListe.add(cursor.getLong(cursor.getColumnIndex("_id")));
            }
        }
        cursor.moveToPosition(position);

        long[] vennIdListe = new long[idListe.size()];

        for(int i = 0; i < idListe.size(); i++) {
            vennIdListe[i] = idListe.get(i);
        }
        return vennIdListe;
    }
}
