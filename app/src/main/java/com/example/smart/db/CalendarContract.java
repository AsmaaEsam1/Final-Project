package com.example.smart.db;

import android.provider.BaseColumns;

public class CalendarContract {
    private CalendarContract(){

    }
    public static final class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COL_UID = BaseColumns._ID;
        public static final String COL_TITLE ="title";
        public static final String COL_DES = "description";
        public static final String COL_LOCATION = "location";
        public static final String COL_STARTTIME = "startTime";
        public static final String COL_ENDTIME = "endTime";
        public static final String CREATE_EVENT_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+COL_UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_TITLE+" TEXT, "+COL_DES+" INTEGER,"+COL_LOCATION+" TEXT, "+COL_STARTTIME+" INTEGER, "+COL_ENDTIME+" TEXT NOT NULL);";
    }
}
