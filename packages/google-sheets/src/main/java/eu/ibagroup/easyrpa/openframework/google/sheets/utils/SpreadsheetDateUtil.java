package eu.ibagroup.easyrpa.openframework.google.sheets.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Contains methods for dealing with Spreadsheet dates.
 */
public class SpreadsheetDateUtil {

    private SpreadsheetDateUtil() {
        // no instances of this class
    }

    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);

    // used to specify that date is invalid
    private static final int BAD_DATE = -1;
    public static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;


    private static final BigDecimal BD_NANOSEC_DAY = BigDecimal.valueOf(SECONDS_PER_DAY * 1e9);
    private static final BigDecimal BD_MILISEC_RND = BigDecimal.valueOf(0.5 * 1e6);
    private static final BigDecimal BD_SECOND_RND = BigDecimal.valueOf(0.5 * 1e9);

    /**
     * Given a LocalDate, converts it into a double representing its internal Spreadsheet representation,
     * which is the number of days since 1/1/1900. Fractional days represent hours, minutes, and seconds.
     *
     * @param date the Date
     * @return Spreadsheet representation of Date (-1 if error - test for error by checking for less than 0.1)
     */
    public static double getSpreadsheetDate(LocalDate date) {
        int year = date.getYear();
        int dayOfYear = date.getDayOfYear();
        int hour = 0;
        int minute = 0;
        int second = 0;
        int milliSecond = 0;

        return internalGetSpreadsheetDate(year, dayOfYear, hour, minute, second, milliSecond);
    }

    /**
     * Given a LocalDateTime, converts it into a double representing its internal Spreadsheet representation,
     * which is the number of days since 1/1/1900. Fractional days represent hours, minutes, and seconds.
     *
     * @param date the Date
     * @return Spreadsheet representation of Date (-1 if error - test for error by checking for less than 0.1)
     */
    public static double getSpreadsheetDate(LocalDateTime date) {
        int year = date.getYear();
        int dayOfYear = date.getDayOfYear();
        int hour = date.getHour();
        int minute = date.getMinute();
        int second = date.getSecond();
        int milliSecond = date.getNano() / 1_000_000;

        return internalGetSpreadsheetDate(year, dayOfYear, hour, minute, second, milliSecond);
    }

    /**
     * Given a Date, converts it into a double representing its internal Spreadsheet representation,
     * which is the number of days since 1/1/1900. Fractional days represent hours, minutes, and seconds.
     *
     * @param date the Date
     * @return Spreadsheet representation of Date (-1 if error - test for error by checking for less than 0.1)
     */
    public static double getSpreadsheetDate(Date date) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(date);
        int year = calStart.get(Calendar.YEAR);
        int dayOfYear = calStart.get(Calendar.DAY_OF_YEAR);
        int hour = calStart.get(Calendar.HOUR_OF_DAY);
        int minute = calStart.get(Calendar.MINUTE);
        int second = calStart.get(Calendar.SECOND);
        int milliSecond = calStart.get(Calendar.MILLISECOND);

        return internalGetSpreadsheetDate(year, dayOfYear, hour, minute, second, milliSecond);
    }

    private static double internalGetSpreadsheetDate(int year, int dayOfYear, int hour, int minute, int second, int milliSecond) {
        if (year < 1900) {
            return BAD_DATE;
        }

        // Because of daylight time saving we cannot use
        //     date.getTime() - calStart.getTimeInMillis()
        // as the difference in milliseconds between 00:00 and 04:00
        // can be 3, 4 or 5 hours but Spreadsheet expects it to always
        // be 4 hours.
        // E.g. 2004-03-28 04:00 CEST - 2004-03-28 00:00 CET is 3 hours
        // and 2004-10-31 04:00 CET - 2004-10-31 00:00 CEST is 5 hours
        double fraction = (((hour * 60.0
                + minute
        ) * 60.0 + second
        ) * 1000.0 + milliSecond
        ) / DAY_MILLISECONDS;

        double value = fraction + absoluteDay(year, dayOfYear);

        if (value >= 60) {
            value++;
        }

        return value;
    }

    /**
     * Given an Spreadsheet date with using 1900 date windowing, and
     * converts it to a java.util.Date.
     * <p>
     * Spreadsheet Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date The Spreadsheet date.
     * @param tz   The TimeZone to evaluate the date in
     * @return Java representation of the date, or null if date is not a valid Spreadsheet date
     */
    public static Date getJavaDate(double date, TimeZone tz) {
        return getJavaDate(date, tz, false);
    }

    /**
     * Given an Spreadsheet date with using 1900 date windowing, and
     * converts it to a java.util.Date.
     * <p>
     * NOTE: If the default <code>TimeZone</code> in Java uses Daylight
     * Saving Time then the conversion back to an Spreadsheet date may not give
     * the same value, that is the comparison
     * <CODE>excelDate == getSpreadsheetDate(getJavaDate(excelDate,false))</CODE>
     * is not always true. For example if default timezone is
     * <code>Europe/Copenhagen</code>, on 2004-03-28 the minute after
     * 01:59 CET is 03:00 CEST, if the excel date represents a time between
     * 02:00 and 03:00 then it is converted to past 03:00 summer time
     *
     * @param date The Spreadsheet date.
     * @return Java representation of the date, or null if date is not a valid Spreadsheet date
     * @see java.util.TimeZone
     */
    public static Date getJavaDate(double date) {
        return getJavaDate(date, null, false);
    }

    /**
     * Given an Spreadsheet date with either 1900 or 1904 date windowing,
     * converts it to a java.util.Date.
     * <p>
     * Spreadsheet Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date         The Spreadsheet date.
     * @param tz           The TimeZone to evaluate the date in
     * @param roundSeconds round to closest second
     * @return Java representation of the date, or null if date is not a valid Spreadsheet date
     */
    public static Date getJavaDate(double date, TimeZone tz, boolean roundSeconds) {
        Calendar calendar = getJavaCalendar(date, tz, roundSeconds);
        return calendar == null ? null : calendar.getTime();
    }

    /**
     * Given an Spreadsheet date with using 1900 date windowing, and
     * converts it to a java.time.LocalDateTime.
     * <p>
     * NOTE: If the default <code>TimeZone</code> in Java uses Daylight
     * Saving Time then the conversion back to an Spreadsheet date may not give
     * the same value, that is the comparison
     * <CODE>excelDate == getSpreadsheetDate(getLocalDateTime(excelDate,false))</CODE>
     * is not always true. For example if default timezone is
     * <code>Europe/Copenhagen</code>, on 2004-03-28 the minute after
     * 01:59 CET is 03:00 CEST, if the excel date represents a time between
     * 02:00 and 03:00 then it is converted to past 03:00 summer time
     *
     * @param date The Spreadsheet date.
     * @return Java representation of the date, or null if date is not a valid Spreadsheet date
     * @see java.util.TimeZone
     */
    public static LocalDateTime getLocalDateTime(double date) {
        return getLocalDateTime(date, false);
    }

    /**
     * Given an Spreadsheet date with either 1900 or 1904 date windowing,
     * converts it to a java.time.LocalDateTime.
     * <p>
     * Spreadsheet Dates and Times are stored without any timezone
     * information. If you know (through other means) that your file
     * uses a different TimeZone to the system default, you can use
     * this version of the getJavaDate() method to handle it.
     *
     * @param date         The Spreadsheet date.
     * @param roundSeconds round to closest second
     * @return Java representation of the date, or null if date is not a valid Spreadsheet date
     */
    @SuppressWarnings("squid:S2111")
    public static LocalDateTime getLocalDateTime(double date, boolean roundSeconds) {
        if (!isValidSpreadsheetDate(date)) {
            return null;
        }

        BigDecimal bd = BigDecimal.valueOf(date);

        int wholeDays = bd.intValue();

        int startYear = 1900;
        int dayAdjust = -1; // Spreadsheet thinks 2/29/1900 is a valid date, which it isn't
        if (wholeDays < 61) {
            // Date is prior to 3/1/1900, so adjust because Spreadsheet thinks 2/29/1900 exists
            // If Spreadsheet date == 2/29/1900, will become 3/1/1900 in Java representation
            dayAdjust = 0;
        }

        LocalDateTime ldt = LocalDateTime.of(startYear, 1, 1, 0, 0);
        ldt = ldt.plusDays(wholeDays + dayAdjust - 1L);

        long nanosTime =
                bd.subtract(BigDecimal.valueOf(wholeDays))
                        .multiply(BD_NANOSEC_DAY)
                        .add(roundSeconds ? BD_SECOND_RND : BD_MILISEC_RND)
                        .longValue();

        ldt = ldt.plusNanos(nanosTime);
        ldt = ldt.truncatedTo(roundSeconds ? ChronoUnit.SECONDS : ChronoUnit.MILLIS);

        return ldt;
    }

    /**
     * Get EXCEL date as Java Calendar with given time zone.
     *
     * @param date         The Spreadsheet date.
     * @param timeZone     The TimeZone to evaluate the date in
     * @param roundSeconds round to closest second
     * @return Java representation of the date, or null if date is not a valid Spreadsheet date
     */
    public static Calendar getJavaCalendar(double date, TimeZone timeZone, boolean roundSeconds) {
        if (!isValidSpreadsheetDate(date)) {
            return null;
        }
        int wholeDays = (int) Math.floor(date);
        int millisecondsInDay = (int) ((date - wholeDays) * DAY_MILLISECONDS + 0.5);
        Calendar calendar;
        if (timeZone != null) {
            calendar = Calendar.getInstance(timeZone);
        } else {
            calendar = Calendar.getInstance(); // using default time-zone
        }
        setCalendar(calendar, wholeDays, millisecondsInDay, roundSeconds);
        return calendar;
    }

    public static void setCalendar(Calendar calendar, int wholeDays, int millisecondsInDay, boolean roundSeconds) {
        int startYear = 1900;
        int dayAdjust = -1; // Spreadsheet thinks 2/29/1900 is a valid date, which it isn't
        if (wholeDays < 61) {
            // Date is prior to 3/1/1900, so adjust because Spreadsheet thinks 2/29/1900 exists
            // If Spreadsheet date == 2/29/1900, will become 3/1/1900 in Java representation
            dayAdjust = 0;
        }
        calendar.set(startYear, Calendar.JANUARY, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, millisecondsInDay);
        if (calendar.get(Calendar.MILLISECOND) == 0) {
            calendar.clear(Calendar.MILLISECOND);
        }
        if (roundSeconds) {
            calendar.add(Calendar.MILLISECOND, 500);
            calendar.clear(Calendar.MILLISECOND);
        }
    }

    /**
     * Given a double, checks if it is a valid Spreadsheet date.
     *
     * @param value the double value
     * @return true if valid
     */
    public static boolean isValidSpreadsheetDate(double value) {
        return (value > -Double.MIN_VALUE);
    }

    /**
     * Given a year and day of year, return the number of days since 1900/12/31.
     *
     * @param dayOfYear the day of the year
     * @param year      the year
     * @return days number of days since 1900/12/31
     * @throws IllegalArgumentException if date is invalid
     */
    private static int absoluteDay(int year, int dayOfYear) {
        return dayOfYear + daysInPriorYears(year);
    }

    /**
     * Return the number of days in prior years since 1900
     *
     * @param yr a year (1900 < yr < 4000)
     * @return days  number of days in years prior to yr.
     * @throws IllegalArgumentException if year is outside of range.
     */
    static int daysInPriorYears(int yr) {
        if (yr < 1900) {
            throw new IllegalArgumentException("'year' must be 1900 or greater");
        }

        int yr1 = yr - 1;
        int leapDays = yr1 / 4   // plus julian leap days in prior years
                - yr1 / 100 // minus prior century years
                + yr1 / 400 // plus years divisible by 400
                - 460;      // leap days in previous 1900 years

        return 365 * (yr - 1900) + leapDays;
    }
}
