package com.github.bzsy.fishbowl.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormat {

    public static String getDateString(Date date, String dateFormat) {
        SimpleDateFormat myDateFormat = new SimpleDateFormat(dateFormat);
        return myDateFormat.format(date);
    }

    public static String longToTimeString(long time) {
        Calendar now = Calendar.getInstance();
        Calendar in = Calendar.getInstance();
        in.setTimeInMillis(time);
        if (in.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            return getDateString(new Date(time), "yyyy/MM/dd HH:mm");
        } else if (in.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR)) {
            return getDateString(new Date(time), "MM/dd HH:mm");
        } else {
            return getDateString(new Date(time), "HH:mm");
        }

    }
}
