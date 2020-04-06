package com.example.smart;

public class Calendars {
    String title;
    String location;
    long endTime;
    long startTime;

    public Calendars(String titles, String locations, long startTimes, long endTimes) {
        title = titles;
        location = locations;
        endTime = endTimes;
        startTime = startTimes;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

}
