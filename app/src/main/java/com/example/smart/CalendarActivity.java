package com.example.smart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.smart.db.CalendarContract.EventEntry;
import com.example.smart.db.DatabaseAdapter;
import com.example.smart.db.DatabaseAdapter.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
   Button intergrationCalendar,calendarEvents;
    EditText eventTitle, eventDescription, eventLocation;
    TextView eventStartTime, eventEndTime;
    int year1, month1, day1, hour1, minute1;
    int year2, month2, day2, hour2, minute2;
    long startMillis;
    long endMillis;
    DatabaseHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        helper = new DatabaseHelper(CalendarActivity.this);

        eventTitle = findViewById(R.id.event_title);
        eventDescription = findViewById(R.id.event_des);
        eventStartTime = findViewById(R.id.event_start_time);
        eventEndTime = findViewById(R.id.event_send_time);
        eventLocation = findViewById(R.id.event_location);
   // show datePicker and time picker to select the start date and time of event
        eventStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                year1 = calendar.get(Calendar.YEAR);
                month1 = calendar.get(Calendar.MONTH);
                day1 = calendar.get(Calendar.DAY_OF_MONTH);
                hour1 = calendar.get(Calendar.HOUR);
                minute1 = calendar.get(Calendar.MINUTE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        year1 = year;
                        month1 = month;
                        day1 = dayOfMonth;
                        calendar.set(year, month, dayOfMonth);

                        TimePickerDialog time = new TimePickerDialog(CalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour1 = hourOfDay;
                                minute1 = minute;
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                eventStartTime.setText(new SimpleDateFormat("dd/MMM/yyyy h:mm a").format(calendar.getTime()));
                            }
                        }, hour1, minute1, DateFormat.is24HourFormat(CalendarActivity.this));
                        time.show();
                    }
                }, year1, month1, day1);
                datePickerDialog.show();

            }

        });

        // show datePicker and time picker to select the end date and time of event
        eventEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                year2 = calendar.get(Calendar.YEAR);
                month2 = calendar.get(Calendar.MONTH);
                day2 = calendar.get(Calendar.DAY_OF_MONTH);
                hour2 = calendar.get(Calendar.HOUR);
                minute2 = calendar.get(Calendar.MINUTE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                                year2 = year;
                                month2 = month;
                                day2 = dayOfMonth;
                                calendar.set(year, month, dayOfMonth);
                                TimePickerDialog time = new TimePickerDialog(CalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        hour2 = hourOfDay;
                                        minute2 = minute;
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        eventEndTime.setText(new SimpleDateFormat("dd/MMM/yyyy h:mm a").format(calendar.getTime()));
                                    }
                                }, hour1, minute1, DateFormat.is24HourFormat(CalendarActivity.this));
                                time.show();
                            }
                        }, year2, month2, day2);
                datePickerDialog.show();
            }
        });

        // integration calendar
        intergrationCalendar = findViewById(R.id.btn_google_calender);
        intergrationCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eventTitle.getText().toString().trim();
                String location = eventLocation.getText().toString().trim();
                String Description = eventDescription.getText().toString().trim();
                //Prepare dates
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(year1, month1, day1, hour1, minute1);
                startMillis = beginTime.getTimeInMillis();
                Calendar endTime = Calendar.getInstance();
                endTime.set(year2, month2, day2, hour2, minute2);
                endMillis = endTime.getTimeInMillis();

                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.setType("vnd.android.cursor.item/event");
                //Add event title
                intent.putExtra(Events.TITLE, title);
                //Add event location
                intent.putExtra(Events.EVENT_LOCATION, location);
                //Add event description
                intent.putExtra(Events.DESCRIPTION, Description);
                // Setting dates
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        startMillis);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        endMillis);
                // make it a full day event
                //  intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                // make it a recurring Event
                intent.putExtra(Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

                // Making it private and shown as busy
                intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
                intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                startActivity(intent);



            }
        });
        calendarEvents = findViewById(R.id.btn_list_calender);
        calendarEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eventTitle.getText().toString().trim();
                String location = eventLocation.getText().toString().trim();
                String startTime = eventStartTime.getText().toString().trim();
                String endTime = eventEndTime.getText().toString().trim();
                if (!title.equals("")) {
                    eventTitle.setText( "No title");
                }
                if (!startTime.equals("")){
                    Calendar cal = Calendar.getInstance();
                    startTime = String.valueOf(cal.getTimeInMillis());
                }
                if (!endTime.equals("")){
                    Calendar cal = Calendar.getInstance();
                    endTime = String.valueOf(cal.getTimeInMillis());
                }
                if (!location.equals("")){
                    // insert the data of event in the database
                    insertEvent(title, location, startTime, endTime);
                }
                // go to the listActivity
                Intent intent1 = new Intent(CalendarActivity.this, EventsActivity.class);
                startActivity(intent1);
            }
        });
    }
    private void insertEvent(String title,String location,String StartTime,String endTime){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("location",location);
        values.put("startTime",StartTime);
        values.put("endTime",endTime);
        long id = db.insert(EventEntry.TABLE_NAME,null,values);
        if (id == -1){
            Toast.makeText(this, "Error with saving data", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Data saved with row id: " + id, Toast.LENGTH_SHORT).show();
        }
    }

}

