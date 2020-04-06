package com.example.smart.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseAdapter {
    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "Events.db";
        private static final int DATABASE_VERSIONS = 1;

        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSIONS);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CalendarContract.EventEntry.CREATE_EVENT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CalendarContract.EventEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
