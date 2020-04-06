package com.example.smart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.smart.db.CalendarContract;
import com.example.smart.db.CalendarContract.EventEntry;
import com.example.smart.db.DatabaseAdapter;
import com.example.smart.db.DatabaseAdapter.DatabaseHelper;

import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity {
    DatabaseHelper helper;
    Button btnClose;
    ArrayList<Calendars> arrayCalendar = new ArrayList<>();
    ListView CalendarListView;
    CalendarAdapter calendarAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        btnClose = findViewById(R.id.btn_close);
        helper = new DatabaseHelper(EventsActivity.this);
        CalendarListView = findViewById(R.id.list_calendar);
        //Get data from database
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] events = {
                EventEntry.COL_TITLE,
                EventEntry.COL_LOCATION,
                EventEntry.COL_STARTTIME,
                EventEntry.COL_ENDTIME
        };

        Cursor c = db.query(EventEntry.TABLE_NAME,events,null,null,null,null,null);
        int titleColumnIndex = c.getColumnIndex(EventEntry.COL_TITLE);
        int locationColumnIndex = c.getColumnIndex(EventEntry.COL_LOCATION);
        int startTimeColumnIndex = c.getColumnIndex(EventEntry.COL_STARTTIME);
        int endTimeColumnIndex = c.getColumnIndex(EventEntry.COL_ENDTIME);

        while (c.moveToNext()) {
            String   eventTitles = c.getString(titleColumnIndex);
            String   eventLocations = c.getString(locationColumnIndex);
            long eventStartTimes = Long.parseLong(c.getString(startTimeColumnIndex));
            long eventEndTimes = Long.parseLong(c.getString(endTimeColumnIndex));
            //then add the data in arraylist
            arrayCalendar.add(new Calendars(eventTitles, eventLocations, eventStartTimes, eventEndTimes));
        }
        calendarAdapter = new CalendarAdapter(EventsActivity.this,arrayCalendar);
        CalendarListView.setAdapter(calendarAdapter);

        //back to the MainActivity
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsActivity.this,CalendarActivity.class);
                startActivity(intent);
            }
        });
    }
}

