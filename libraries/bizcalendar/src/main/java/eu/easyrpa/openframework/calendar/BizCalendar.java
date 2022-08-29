package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Provides base functionality to work with working days.
 * <p>
 * With this class you can easily check whether the certain date is a working day or a holiday, calculate how many
 * working days or holidays between two current dates etc.
 */
public class BizCalendar {

    /**
     * List of Holiday Entity objects, which contains all holidays of a certain country.
     * <p>
     * All information about holidays is located in the DataStore on the EasyRPA Control Server
     */
    private final List<HolidayEntity> holidayEntities;

    /**
     * List of holidays, which contains only "Other Holidays" - specific date that is not a working day.
     */
    private final List<HolidayEntity> otherHolidays;

    /**
     * List of holidays, which contains only "Public Holidays" - official holidays of a country.
     */
    private final List<HolidayEntity> publicHolidays;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    //TODO: Ask what will be better: two parameters or only HolidayRepository as a parameter
    public BizCalendar(HolidayRepository holidayRepository) {
        this.holidayEntities = holidayRepository.findAll_("*COMMON_DS_NAME OR REGION*");
        this.otherHolidays = holidayRepository.findAllOtherHolidays_("*COMMON_DS_NAME OR REGION*");
        this.publicHolidays = holidayRepository.findAllPublicHolidays_("*COMMON_DS_NAME OR REGION*");
    }

    /**
     * Adds the specified number of working days to a start date returning the result.
     * <p>
     * In order to get the next working day after a known date, use a 'Days' value of 1. To get the previous working
     * day, use a value of -1. Note that calling this action with a Days value of 0 will always return Start Date,
     * regardless of whether that is a working day or not.
     *
     * @param startDate                the date to which the number of working days should be added.
     * @param numberOfWorkingDaysToAdd The number of days to be added. Note that a value of zero will always return
     *                                 Start Date, even if it is not a working day.
     * @return The date with the specified number of days added to it.
     */
    public LocalDate addWorkingDays(LocalDate startDate, int numberOfWorkingDaysToAdd) {
        if (numberOfWorkingDaysToAdd < 0) {
            numberOfWorkingDaysToAdd = Math.abs(numberOfWorkingDaysToAdd);
            for (int i = 0; i < numberOfWorkingDaysToAdd; i++) {
                if (isWorkingDay(startDate)) {
                    startDate = startDate.minusDays(1);
                } else {
                    startDate = startDate.minusDays(1);
                    numberOfWorkingDaysToAdd++;
                }
            }
        }
        if (numberOfWorkingDaysToAdd > 0) {
            for (int i = 0; i < numberOfWorkingDaysToAdd; i++) {
                if (isWorkingDay(startDate)) {
                    startDate = startDate.plusDays(1);
                } else {
                    startDate = startDate.plusDays(1);
                    numberOfWorkingDaysToAdd++;
                }
            }
        }
        return startDate;
    }

    /**
     * Counts the number of working days found within the given range.
     *
     * @param startDate is a LocalDate object which represents the start of the range.
     * @param endDate   is a LocalDate object which represents the end of the range.
     * @return the number of working days in range in int type.
     */
    public int countWorkingDaysInRange(LocalDate startDate, LocalDate endDate) {
        int result = 0;
        while (!startDate.isEqual(endDate.plusDays(1))) {
            if (isWorkingDay(startDate)) {
                result++;
            }
            startDate = startDate.plusDays(1);
        }
        return result;
    }

    /**
     * Gets all the other holidays specified in a DataStore which fall within the (inclusive) date range given.
     *
     * @param startDate The first date to consider other holidays from.
     * @param endDate   The last date to consider other holidays until.
     * @return The list of other holidays which fall between the specified dates inclusively.
     */
    public List<HolidayEntity> getOtherHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        List<HolidayEntity> result = new ArrayList<>();

        while (!startDate.isEqual(endDate)) {
            if (isOtherHoliday(startDate)) {
                result.add(checkDate(otherHolidays, startDate));
                startDate = startDate.plusDays(1);
            }
        }

        return result;
    }

    /**
     * Gets all the public holidays defined is a DataStore which fall within the (inclusive) date range given.
     *
     * @param startDate The first date to consider other holidays from.
     * @param endDate   The last date to consider other holidays until.
     * @return The list of public holidays which fall between the specified dates inclusively.
     */
    public List<HolidayEntity> getPublicHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        List<HolidayEntity> result = new ArrayList<>();

        while (!startDate.isEqual(endDate)) {
            if (isPublicHoliday(startDate)) {
                result.add(checkDate(publicHolidays, startDate));
                startDate = startDate.plusDays(1);
            }
        }

        return result;
    }

    /**
     * Gets the working days as configured on the specified calendar, starting and ending on the specified dates.
     *
     * @param startDate the first date to consider working days from.
     * @param endDate   the last date to consider working days until.
     * @return the working days which fall between the specified dates (inclusive).
     */
    public List<LocalDate> getWorkingDaysInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> result = new ArrayList<>();

        while (!startDate.isEqual(endDate)) {
            if (isWorkingDay(startDate)) {
                result.add(startDate);
                startDate = startDate.plusDays(1);
            }
        }

        return result;
    }

    /**
     * Checks if the given date represents a date defined as an 'other holiday' in the specified DataStore.
     *
     * @param date the date to check.
     * @return True to indicate that the given date falls on another holiday; False otherwise.
     */
    public boolean isOtherHoliday(LocalDate date) {
        for (HolidayEntity holiday : otherHolidays) {
            if(holiday.getType() == HolidayEntity.HolidayType.MOVING){
                LocalDate movingDate = movingDateToLocalDate(holiday);
                if(movingDate.isEqual(date)){
                    return true;
                }
            }
            if (date.getMonthValue() == holiday.getMonth() && date.getDayOfMonth() == holiday.getDay()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given date represents a public holiday as defined in the specified DataStore.
     *
     * @param date the date to check
     * @return True to indicate that the given date falls on a public holiday; False otherwise
     */
    public boolean isPublicHoliday(LocalDate date) {
        for (HolidayEntity holiday : publicHolidays) {
            if(holiday.getType() == HolidayEntity.HolidayType.MOVING){
                LocalDate movingDate = movingDateToLocalDate(holiday);
                if(movingDate.isEqual(date)){
                    return true;
                }
            }
            if (date.getMonthValue() == holiday.getMonth() && date.getDayOfMonth() == holiday.getDay()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given date represents a weekend.
     *
     * @param date the date to check
     * @return True to indicate that the given date falls on a weekend; False otherwise
     */
    public boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SUNDAY && date.getDayOfWeek() != DayOfWeek.SATURDAY;
    }

    /**
     * Checks if the given date represents a working day as defined in the specified DataStore.
     *
     * @param date the date to check
     * @return True to indicate that the given date falls on a working day; False otherwise
     */
    public boolean isWorkingDay(LocalDate date) {
        if (isWeekend(date) || isPublicHoliday(date) || isOtherHoliday(date)) {
            return false;
        }

        return date.getDayOfWeek() != DayOfWeek.MONDAY
                || (!isPublicHoliday(date.minusDays(2)) && !isPublicHoliday(date.minusDays(1)));
    }

    /**
     * Finds and returns whether public or other holidays, within the specified range.
     *
     * @param holidayEntities the list of given holidays, whether public or others.
     * @param date            the date to check what public or other holiday it is.
     * @return list oh holidays, whether public or others.
     */
    private HolidayEntity checkDate(List<HolidayEntity> holidayEntities, LocalDate date) {
        for (HolidayEntity holidayEntity : holidayEntities) {
            if (date.getMonthValue() == holidayEntity.getMonth() && date.getDayOfMonth() == holidayEntity.getDay()) {
                return holidayEntity;
            }
        }
        return null;
    }

    /**
     * Parses HolidayEntity with HolidayType.MOVING to LocalDate object.
     *
     * @param holiday a holidayEntity instance with a moving date.
     * @return LocalDate object.
     */
    private LocalDate movingDateToLocalDate(HolidayEntity holiday) {
        String[] dates = holiday.getDateOfMovingHoliday().split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E");
        TemporalAccessor accessor = formatter.parse(dates[1]);

        LocalDate start = LocalDate.of(holiday.getValidFrom(), holiday.getMonth(), 1);
        return start.with(TemporalAdjusters.dayOfWeekInMonth(Integer.parseInt(dates[0]), DayOfWeek.from(accessor)));
    }

}
// public boolean isWorkingDay(LocalDate date) {
//        if (isWeekend(date)) {
//            return false;
//        } else {
//            for (HolidayEntity holidayEntity : holidayEntities) {
//                if (holidayEntity.getMonth() == date.getMonthValue()
//                        && holidayEntity.getDay() == date.getDayOfMonth()) {
//                    if (holidayEntity.isSubstitute()) {
//                        LocalDate weekendDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
//                        while (!isWeekend(weekendDate)) {
//                            weekendDate = weekendDate.plusDays(1);
//                        }
//                    }
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

//    public boolean isOtherHoliday(LocalDate date) {
//        AtomicBoolean isOtherHoliday = new AtomicBoolean(false);
//        holidayEntities.forEach(holidayEntity -> {
//            if(holidayEntity.getMonth() == date.getMonthValue()
//                    && holidayEntity.getDay() == date.getDayOfMonth()
//                    && holidayEntity.isCustomHoliday()) {
//                isOtherHoliday.set(true);
//            }
//        });
//        return isOtherHoliday.get();
//    }

//    public boolean isPublicHoliday(LocalDate date) {
//        AtomicBoolean isPublicHoliday = new AtomicBoolean(false);
//        if(!isOtherHoliday(date)) {
//            holidayEntities.forEach(holidayEntity -> {
//                if (holidayEntity.getMonth() == date.getMonthValue()
//                        && holidayEntity.getDay() == date.getDayOfMonth()) {
//                    isPublicHoliday.set(true);
//                }
//            });
//        }
//        return isPublicHoliday.get();
//    }
