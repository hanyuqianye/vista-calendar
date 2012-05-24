package com.common.vistacalendar;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Dmitry Savchenko
 */
public class DateExt {

    public Calendar calendar;

    public DateExt(Date date) {
        init();
        if(date!=null){
            calendar.setTime(date);
        }
    }

    public DateExt(long milliseconds) {
        init();
        calendar.setTime(new Date(milliseconds));
    }

    public DateExt() {
        init();
    }

    private void init() {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
    }

    /**
     * Sets what the first day of the week is; e.g., <code>SUNDAY</code> in the U.S.,
     * <code>MONDAY</code> in France.
     * @param value the given first day of the week.
     */
    public void setFirstDayOfWeek(int day) {
        calendar.setFirstDayOfWeek(day);
    }

    /**
     * Gets what the first day of the week is; e.g., <code>SUNDAY</code> in the U.S.,
     * <code>MONDAY</code> in France
     * @return the first day of the week.
     */
    public int getFirstDayOfWeek() {
        return calendar.getFirstDayOfWeek();
    }

    public DateExt setDate(Date date) {
        calendar.setTime(date);
        return this;
    }

    public Date getDate() {
        return calendar.getTime();
    }

    /**
     * Sets this <code>Date</code> object to represent a point in time that is 
     * <code>time</code> milliseconds after January 1, 1970 00:00:00 GMT. 
     *
     * @param   time   the number of milliseconds.
     */
    public void setTime(long time) {
        setDate(new Date(time));
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>Date</tt> object.
     *
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this date.
     */
    public long getTime() {
        return getDate().getTime();
    }

    public DateExt setDay(int day) {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return this;
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public DateExt setMonth(int month) {
        calendar.set(Calendar.MONTH, month);
        return this;
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH);
    }

    public DateExt setDecade(int decade) {
        Date lastDate = calendar.getTime();
        int year = getYear();
        int delta = year % 10;
        decade += delta;
        setYear(decade);
        year = getYear();
        if (year < 1900 || year > 2500) {
            calendar.setTime(lastDate);
        }
        return this;
    }

    public int getDecade() {
        int year = getYear();
        return year - (year % 10);
    }

    public DateExt setYear(int year) {
        Date lastDate = calendar.getTime();
        calendar.set(Calendar.YEAR, year);
        year = getYear();
        if (year < 1900 || year > 2500) {
            calendar.setTime(lastDate);
        }
        return this;
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Sets the hour, minute, seconds, milliseconds to 0.
     * @return 
     */
    public DateExt clearTime() {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return this;
    }

    public DateExt setHour(int hour) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return this;
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public DateExt setMinute(int minute) {
        calendar.set(Calendar.MINUTE, minute);
        return this;
    }

    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public DateExt setSeconds(int seconds) {
        calendar.set(Calendar.SECOND, seconds);
        return this;
    }

    public int getSeconds() {
        return calendar.get(Calendar.SECOND);
    }

    /**
     * Set the millisecond in the second.
     * E.g., at 10:04:15.898 PM the <code>MILLISECOND</code> is 898.
     */
    public DateExt setMilliseconds(int milliseconds) {
        calendar.set(Calendar.MILLISECOND, milliseconds);
        return this;
    }

    /**
     * Returns the millisecond within the second.
     * E.g., at 10:04:15.250 PM the <code>MILLISECOND</code> is 250.
     */
    public int getMilliseconds() {
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * Add the <i>count</i> of days to the current date
     * @param count
     * can be positive or negative value
     * @return 
     * current object
     */
    public DateExt addDay(int count) {
        calendar.add(Calendar.DAY_OF_MONTH, count);
        return this;
    }

    public DateExt addMonth(int count) {
        Date lastDate = calendar.getTime();
        calendar.add(Calendar.MONTH, count);
        int year = getYear();
        if (year < 1900 || year > 2500) {
            calendar.setTime(lastDate);
        }
        return this;
    }

    public DateExt addHour(int count) {
        calendar.add(Calendar.HOUR_OF_DAY, count);
        return this;
    }

    public DateExt addMinute(int count) {
        calendar.add(Calendar.MINUTE, count);
        return this;
    }

    public DateExt addSecond(int count) {
        calendar.add(Calendar.SECOND, count);
        return this;
    }

    public DateExt addYear(int count) {
        Date lastDate = calendar.getTime();
        calendar.add(Calendar.YEAR, count);
        int year = getYear();
        if (year < 1900 || year > 2500) {
            calendar.setTime(lastDate);
        }
        return this;
    }

    /**
    
     * @return 
     * Returns the count of the days in current month
     */
    public int getDaysInMonth() {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public DateExt clone() {
        DateExt date = new DateExt();
        date.setDate(getDate());
        date.setFirstDayOfWeek(getFirstDayOfWeek());
        return date;
    }

    public int getDayOfWeek() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public String toString() {
        return getDate().toString();
    }

    public boolean equals(DateExt obj) {
        if (obj == null) {
            return false;
        }
        return getDate().equals(obj.getDate());
    }
}