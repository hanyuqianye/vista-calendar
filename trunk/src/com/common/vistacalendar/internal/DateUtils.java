package com.common.vistacalendar.internal;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Dmitry Savchenko
 */
public class DateUtils {

    public static int getDayOfWeekFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date day = c.getTime();
        return c.get(Calendar.DAY_OF_WEEK);
    }
    //SUN - 1, MON - 2
    //But if we define the start day as monday, we should convert number to MON - 1 ... SUN - 7

    public static int dayOfWeekToCurrent(int weekDay, int startDay) {
        int day = weekDay - (startDay - 1);
        if (day > 7) {
            day = day % 7;
        }
        if (day < 1) {
            day = 7 - day;
        }
        return day;
    }

    public static int currentToDayOfWeek(int day, int startDay) {
        day = day + (startDay - 1);
        if (day > 7) {
            day = day % 7;
        }
        if (day < 1) {
            day = 7 - day;
        }
        return day;
    }
}
