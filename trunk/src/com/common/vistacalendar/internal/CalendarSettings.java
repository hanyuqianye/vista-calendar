package com.common.vistacalendar.internal;

import java.awt.Color;
import java.text.DateFormatSymbols;

/**
 * @author Dmitry Savchenko
 */
public class CalendarSettings {

    private String[] dayNamesShort = new String[8];
    private String[] dayNamesLong = new String[8];
    private String[] monthNamesShort = new String[12];
    private String[] monthNamesLong = new String[12];
    public static Color BACKGROUNDCOLOR=new Color(213,237,255);

    public CalendarSettings() {
        init();
    }
    
    private void init(){
        dayNamesShort = (new DateFormatSymbols()).getShortWeekdays();
        dayNamesLong = (new DateFormatSymbols()).getWeekdays();
        monthNamesShort = (new DateFormatSymbols()).getShortMonths();
        monthNamesLong=(new DateFormatSymbols()).getMonths();
        setDayNamesShort(new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"});
    }

    public int getStartDay() {
        return 2;
    }

    public String getDayNameShort(int day) {
        day = DateUtils.currentToDayOfWeek(day, getStartDay());
        return dayNamesShort[day];
    }

    public String getDayNameLong(int day) {
        day = DateUtils.currentToDayOfWeek(day, getStartDay());
        return dayNamesLong[day];
    }

    public void setDayNamesShort(String[] days) {
        dayNamesShort[0] = "";
        for (int i = 1; i < 8; i++) {
            dayNamesShort[i] = days[i - 1];
        }
    }

    public String getShortMonthName(int month) {
        return monthNamesShort[month];
    }
    
    public String getLongMonthName(int month){
        return monthNamesLong[month];
    }
}