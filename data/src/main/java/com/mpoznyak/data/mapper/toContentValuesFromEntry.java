package com.mpoznyak.data.mapper;

import android.content.ContentValues;
import com.mpoznyak.domain.model.Entry;

import static com.mpoznyak.data.DatabaseHelper.DatabaseContract.*;


public class toContentValuesFromEntry {

    private static final String CASE_ID = COLUMN_CASE_ID;
    private static final String PATH = COLUMN_PATH;

    public static ContentValues map(Entry entry) {
        ContentValues values = new ContentValues();
        values.put(CASE_ID, entry.getCase_Id());
        values.put(PATH, entry.getPath().toString());
        return values;

    }

}
