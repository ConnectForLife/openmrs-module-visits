package org.openmrs.module.visits.api.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.openmrs.module.visits.api.util.VisitsConstants.DEFAULT_FRONT_END_DATE_FORMAT;
import static org.openmrs.module.visits.api.util.VisitsConstants.DEFAULT_SERVER_SIDE_DATETIME_FORMAT;
import static org.openmrs.module.visits.api.util.VisitsConstants.DEFAULT_SERVER_SIDE_DATE_FORMAT;

public final class DateUtil {

    public static final int HOUR_IN_SECONDS = 60 * 60;
    public static final int DAY_IN_SECONDS = 24 * HOUR_IN_SECONDS;

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public static Date now() {
        return getDateWithDefaultTimeZone(new Date());
    }

    public static Date getDatePlusSeconds(long seconds) {
        return DateUtils.addSeconds(DateUtil.now(), (int) seconds);
    }

    public static Date getDatePlusDays(Date date, int numberOfDays) {
        return DateUtils.addDays(date, numberOfDays);
    }

    public static Date getDatePlusMonths(Date date, int numberOfMonths) {
        return DateUtils.addMonths(date, numberOfMonths);
    }

    public static boolean isNotAfter(Date first, Date second) {
        return !first.after(second);
    }

    public static Date getDateWithDefaultTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, getDefaultTimeZone());
    }

    public static Date getDateWithTimeZone(Date timestamp, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(timestamp);
        return calendar.getTime();
    }

    public static Date getDateSecondsAgo(long seconds) {
        return DateUtils.addSeconds(DateUtil.now(), (int) (seconds * -1));
    }

    public static boolean isSameInstant(Date date1, Date date2) {
        return DateUtils.isSameInstant(date1, date2);
    }

    public static TimeZone getDefaultTimeZone() {
        return DEFAULT_TIME_ZONE;
    }

    public static String convertServerSideDateFormatToFrontend(String date) throws ParseException {
        return convertDate(date, DEFAULT_SERVER_SIDE_DATE_FORMAT, DEFAULT_FRONT_END_DATE_FORMAT);
    }

    public static String convertFrontendDateFormatToServerSide(String date) throws ParseException {
        return convertDate(date, DEFAULT_FRONT_END_DATE_FORMAT, DEFAULT_SERVER_SIDE_DATE_FORMAT);
    }

    public static String convertToServerSideDateTime(Date date) {
        return convertDate(date, DEFAULT_SERVER_SIDE_DATETIME_FORMAT);
    }

    public static Date toSimpleDate(Date date) {
        if (date instanceof java.sql.Date) { // java.sql.Date cannot be persisted by hibernate
            return new Date(date.getTime());
        }
        return date;
    }

    public static Date getDateIgnoringTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getLastDayOfCurrentWeekDateFromGivenDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));

        return calendar.getTime();
    }

    public static Date getLastDayOfCurrentMonthDateFromGivenDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }

    public static Date convertStringToDate(String date, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(date);
    }

    public static String convertDate(Date date, String toFormat) {
        return new SimpleDateFormat(toFormat).format(date);
    }

    private static String convertDate(String date, String fromFormat, String toFormat) throws ParseException {
        SimpleDateFormat newDateFormat = new SimpleDateFormat(fromFormat);
        Date myDate = newDateFormat.parse(date);
        newDateFormat.applyPattern(toFormat);
        return newDateFormat.format(myDate);
    }

    private DateUtil() { }
}
