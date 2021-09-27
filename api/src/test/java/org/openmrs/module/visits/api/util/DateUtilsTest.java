package org.openmrs.module.visits.api.util;

import org.junit.Test;
import org.openmrs.module.visits.BaseTest;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.visits.TestConstants.MILLISECONDS_PER_DAY;

public class DateUtilsTest extends BaseTest {

    @Test
    public void todayShouldReturnDateWithoutTime() {
        Date dateWithoutTime = DateUtils.today();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateWithoutTime);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(dateWithoutTime.getTime() + MILLISECONDS_PER_DAY - 1);
        assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void isTodayOrAfterShouldReturnTrueIfToday() {
        Date today = DateUtils.today();
        assertTrue(DateUtils.isTodayOrAfter(today));
    }

    @Test
    public void isTodayOrAfterShouldReturnTrueIfAfterToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtils.today().getTime() + MILLISECONDS_PER_DAY + 1);
        Date nextDay = calendar.getTime();

        assertTrue(DateUtils.isTodayOrAfter(nextDay));
    }

    @Test
    public void isTodayOrAfterShouldReturnFalseIfBeforeToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtils.today().getTime() - 1);
        Date nextDay = calendar.getTime();

        assertFalse(DateUtils.isTodayOrAfter(nextDay));
    }

    @Test
    public void isBeforeTodayShouldReturnTrueIfBeforeToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtils.today().getTime() - 1);
        Date previousDate = calendar.getTime();

        assertTrue(DateUtils.isBeforeToday(previousDate));
    }

    @Test
    public void isBeforeTodayShouldReturnFalseIfAfterToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtils.today().getTime() + MILLISECONDS_PER_DAY + 1);
        Date nextDay = calendar.getTime();

        assertFalse(DateUtils.isBeforeToday(nextDay));
    }

    @Test
    public void isFutureTodayShouldReturnTrueIfFutureDate() {
        Date now = DateUtil.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now.getTime() + MILLISECONDS_PER_DAY);
        Date futureDate = calendar.getTime();

        assertTrue(DateUtils.isFuture(futureDate));
    }

    @Test
    public void isFutureTodayShouldReturnFalseIfPastDate() {
        Date now = DateUtil.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now.getTime() - MILLISECONDS_PER_DAY);
        Date pastDate = calendar.getTime();

        assertFalse(DateUtils.isFuture(pastDate));
    }

    @Test
    public void isPastTodayShouldReturnTrueIfPastDate() {
        Date now = DateUtil.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now.getTime() - MILLISECONDS_PER_DAY);
        Date futureDate = calendar.getTime();

        assertTrue(DateUtils.isPast(futureDate));
    }

    @Test
    public void isPastTodayShouldReturnFalseIfFutureDate() {
        Date now = DateUtil.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now.getTime() + MILLISECONDS_PER_DAY);
        Date pastDate = calendar.getTime();

        assertFalse(DateUtils.isPast(pastDate));
    }
}
