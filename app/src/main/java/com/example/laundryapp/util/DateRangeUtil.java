package com.example.laundryapp.util;

import java.util.Calendar;

public class DateRangeUtil {

    public static long startOfDayMillis(int year, int month0, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month0);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long endOfDayMillis(int year, int month0, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startOfDayMillis(year, month0, day));
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTimeInMillis() - 1;
    }

    public static long startOfToday() {
        Calendar cal = Calendar.getInstance();
        return startOfDayMillis(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public static long endOfToday() {
        Calendar cal = Calendar.getInstance();
        return endOfDayMillis(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public static long startOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return startOfDayMillis(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
    }

    public static long endOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        long nextMonthStart = startOfDayMillis(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
        return nextMonthStart - 1;
    }
}
