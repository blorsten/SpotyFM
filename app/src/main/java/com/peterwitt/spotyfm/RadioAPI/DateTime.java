package com.peterwitt.spotyfm.RadioAPI;

import java.util.Calendar;

public class DateTime{
    private enum dayName{
        sunday, monday, tuesday, wednsday, thursday, friday, saturday
    }

    public int year;
    public int month;
    public int day;
    public int dayOfWeek;
    public int hour;
    public int minute;

    public DateTime(){
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    }

    public String getNameOfDay(){
        return dayName.values()[dayOfWeek - 1].toString();
    }

    public String getCurrentDate(){
        return year + "-" + month + "-" + day;
    }

    public String getCurrentTime(){
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }
}