package com.peterwitt.spotyfm.RadioAPI;

import java.util.Calendar;

public class DateTime{
    private enum dayName{
        sunday, monday, tuesday, wednesday, thursday, friday, saturday
    }

    private int year;
    private int month;
    private int day;
    private int dayOfWeek;
    public int hour;
    public int minute;

    private boolean isSetToCustom;

    public void setSetToCustom(boolean setToCustom) {
        isSetToCustom = setToCustom;
    }

    public DateTime(){
        getTime();
    }

    private void getTime(){
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    }

    public void resetTime(){
        isSetToCustom = false;
        getTime();
    }

    public String getNameOfDay(){
        if(!isSetToCustom)
            getTime();

        return dayName.values()[dayOfWeek - 1].toString();
    }

    public String getCurrentDate(){
        if(!isSetToCustom)
            getTime();

        return year + "-" + month + "-" + day;
    }

    public String getCurrentTime(){
        if(!isSetToCustom)
            getTime();

        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }
}