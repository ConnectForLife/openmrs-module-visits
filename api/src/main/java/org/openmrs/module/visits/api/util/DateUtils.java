package org.openmrs.module.visits.api.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

    public static Date now() {
        return new Date();
    }

    public static Date today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean isFuture(Date date) {
        return date.after(now());
    }

    public static boolean isPast(Date date) {
        return !isFuture(date);
    }

    public static boolean isTodayOrAfter(Date date) {
        return !isBeforeToday(date);
    }

    public static boolean isBeforeToday(Date date) {
        return date.before(today());
    }

    private DateUtils() { }
}
