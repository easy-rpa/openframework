package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.constants.BizCalendarConfigParam;
import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;
import eu.easyrpa.openframework.calendar.utils.Converter;
import eu.easyrpa.openframework.core.sevices.RPAServicesAccessor;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides base functionality to work with working days.
 * <p>
 * With this class you can easily check whether the certain date is a working day or a holiday, calculate how many
 * working days or holidays are there between two current dates etc.
 */
public class BizCalendar {

    private static final String DEFAULT_DS_NAME = "USA";
    private static final String DEFAULT_WEEKENDS = "Sat,Sun";

    /**
     * List of {@code HolidayEntity} objects, which contains all holidays of a certain country.
     * <p>
     * All information about holidays is located in the DataStore on the EasyRPA Control Server.
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
     * Name of the data store, where holidays are hold.
     */
    private String dsName;

    /**
     * List of days of week that are weekends.
     */
    private List<DayOfWeek> weekends;

    /**
     * Instance of RPA services accessor that allows to get configuration parameters and secret vault entries from
     * RPA platform.
     */
    private RPAServicesAccessor rpaServices;

    /**
     * Default constructor for {@code BizCalendar}.
     *
     * @param holidayRepository an instance of {@code HolidayRepository}.
     */
    public BizCalendar(HolidayRepository holidayRepository) {
        this.dsName = getDsName();
        this.weekends = getWeekends();
        this.holidayEntities = holidayRepository.findAll_(dsName);
        this.otherHolidays = holidayRepository.findAllOtherHolidays_(dsName);
        this.publicHolidays = holidayRepository.findAllPublicHolidays_(dsName);
    }

    //TODO: fix fix fix fix
    @Inject
    public BizCalendar(HolidayRepository holidayRepository, RPAServicesAccessor rpaServices) {
        this(holidayRepository);
        this.rpaServices = rpaServices;
    }

    /**
     * Gets data store name, where holidays are hold.
     *
     * @return data sore name where holidays are holds.
     */
    public String getDsName() {
        if (dsName == null) {
            dsName = getConfigParam(BizCalendarConfigParam.DATASTORE_NAME);
            if (dsName == null) {
                dsName = DEFAULT_DS_NAME;
            }
        }
        return dsName;
    }

    /**
     * Gets list of {@link DayOfWeek}, which are weekends.
     *
     * @return list of {@link DayOfWeek}, which are weekends
     */
    public List<DayOfWeek> getWeekends() {
        String daysOfWeek = "";
        if (weekends == null) {
            daysOfWeek = getConfigParam(BizCalendarConfigParam.WEEKENDS);
        }
        if (weekends == null) {
            daysOfWeek = DEFAULT_WEEKENDS;
        }
        List<String> buf = Arrays.asList(Objects.requireNonNull(daysOfWeek).split(","));
        weekends = buf.stream().map(this::fromStringToDayOfWeek).collect(Collectors.toList());
        return weekends;
    }

    /**
     * Returns all public holidays of a country.
     *
     * @return list of public holidays.
     */
    public List<HolidayEntity> getPublicHolidays() {
        return publicHolidays;
    }

    /**
     * Returns all other holidays of a country.
     *
     * @return list of other holidays.
     */
    public List<HolidayEntity> getOtherHolidays() {
        return otherHolidays;
    }

    /**
     * Returns all holidays of a country.
     *
     * @return list of all holidays.
     */
    public List<HolidayEntity> getAllHolidays() {
        return holidayEntities;
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
        int count = 0;
        if (numberOfWorkingDaysToAdd < 0) {
            numberOfWorkingDaysToAdd = Math.abs(numberOfWorkingDaysToAdd);
            while (count != numberOfWorkingDaysToAdd) {
                if (isWorkingDay(startDate.minusDays(1))) {
                    count++;
                }
                startDate = startDate.minusDays(1);
            }
            return startDate;
        }
        if (numberOfWorkingDaysToAdd > 0) {
            while (count != numberOfWorkingDaysToAdd) {
                if (isWorkingDay(startDate.plusDays(1))) {
                    count++;
                }
                startDate = startDate.plusDays(1);
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

        if (endDate.isAfter(startDate)) {
            while (!startDate.isEqual(endDate.plusDays(1))) {
                if (isWorkingDay(startDate)) {
                    result++;
                }
                startDate = startDate.plusDays(1);
            }
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

        if (endDate.isAfter(startDate)) {
            while (!startDate.isEqual(endDate.plusDays(1))) {
                if (isOtherHoliday(startDate)) {
                    result.add(findHoliday(otherHolidays, startDate));
                }
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

        if (endDate.isAfter(startDate)) {
            while (!startDate.isEqual(endDate.plusDays(1))) {
                if (isPublicHoliday(startDate)) {
                    result.add(findHoliday(publicHolidays, startDate));
                }
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

        if (endDate.isAfter(startDate)) {
            while (!startDate.isEqual(endDate.plusDays(1))) {
                if (isWorkingDay(startDate)) {
                    result.add(startDate);
                }
                startDate = startDate.plusDays(1);
            }
        }
        return result;
    }

    /**
     * Checks if the given date represents a date defined as an 'other holiday' in the DataStore.
     *
     * @param date the date to check.
     * @return True to indicate that the given date falls on another holiday; False otherwise.
     */
    public boolean isOtherHoliday(LocalDate date) {
        return findHoliday(otherHolidays, date) != null;
    }

    /**
     * Checks if the given date represents a public holiday as defined in the specified DataStore.
     *
     * @param date the date to check.
     * @return True to indicate that the given date falls on a public holiday; False otherwise.
     */
    public boolean isPublicHoliday(LocalDate date) {
        return findHoliday(publicHolidays, date) != null;
    }

    /**
     * Checks if the given date represents a weekend.
     *
     * @param date the date to check.
     * @return True to indicate that the given date falls on a weekend; False otherwise.
     */
    public boolean isWeekend(LocalDate date) {
        return weekends.contains(date.getDayOfWeek());
    }

    /**
     * Checks if the given date represents a working day as defined in the specified DataStore.
     *
     * @param date the date to check.
     * @return True to indicate that the given date falls on a working day; False otherwise.
     */
    public boolean isWorkingDay(LocalDate date) {
        if (isWeekend(date) || isPublicHoliday(date) || isOtherHoliday(date)) {
            return false;
        }

        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            if (isPublicHoliday(date.minusDays(1)) || isPublicHoliday(date.minusDays(2))) {
                for (HolidayEntity holidayEntity : publicHolidays) {
                    if (date.minusDays(1).getMonthValue() == holidayEntity.getMonth() &&
                            date.minusDays(1).getDayOfMonth() == holidayEntity.getDay() &&
                            holidayEntity.isSubstitute()) {
                        return false;
                    }
                    if (date.minusDays(2).getMonthValue() == holidayEntity.getMonth() &&
                            date.minusDays(2).getDayOfMonth() == holidayEntity.getDay() &&
                            holidayEntity.isSubstitute()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Finds and returns whether public or other holiday.
     *
     * @param holidayEntities the list of given holidays, whether public or others.
     * @param date            the date to check what public or other holiday it is.
     * @return list oh holidays, whether public or others.
     */
    private HolidayEntity findHoliday(List<HolidayEntity> holidayEntities, LocalDate date) {
        for (HolidayEntity holiday : holidayEntities) {
            if (holiday.isChurchHoliday()) {
                if (holiday.getChurchHolidayType() == HolidayEntity.ChurchHolidayType.ISLAMIC) {
                    LocalDate date1 = Converter.fromHijriToLocal(holiday);
                    if (date1.isEqual(date)) {
                        return holiday;
                    }
                }

                if (holiday.getChurchHolidayType() == HolidayEntity.ChurchHolidayType.ORTHODOX) {
                    LocalDate date1 = Converter.getFloatingChurchHolidayDate(holiday);
                    if (date1.equals(date)) {
                        return holiday;
                    }
                }

                if (holiday.getChurchHolidayType() == HolidayEntity.ChurchHolidayType.CATHOLIC) {
                    LocalDate date1 = Converter.getFloatingChurchHolidayDate(holiday);
                    if (date1.equals(date)) {
                        return holiday;
                    }
                }
                continue;
            }

            if (holiday.getType() == HolidayEntity.HolidayType.FLOATING) {
                LocalDate movingDate = floatingDateToLocalDate(holiday);
                if (movingDate.isEqual(date)) {
                    return holiday;
                }
            }

            if (date.getMonthValue() == holiday.getMonth() && date.getDayOfMonth() == holiday.getDay()) {
                return holiday;
            }
        }
        return null;
    }

    /**
     * Parses HolidayEntity with {@code HolidayType.MOVING} to {@link LocalDate} object.
     *
     * @param holiday a {@code HolidayEntity} instance with a moving date.
     * @return parsed {@link LocalDate} object.
     */
    private LocalDate floatingDateToLocalDate(HolidayEntity holiday) {
        String[] dates = holiday.getDateOfFloatingHoliday().split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E");
        TemporalAccessor accessor = formatter.parse(dates[1]);

        LocalDate start = LocalDate.of(holiday.getValidFrom(), holiday.getMonth(), 1);
        return start.with(TemporalAdjusters.dayOfWeekInMonth(Integer.parseInt(dates[0]), DayOfWeek.from(accessor)));
    }

    /**
     * Parses String to {@link DayOfWeek}
     *
     * @param dayOfWeek represents day of week in String type.
     * @return parsed {@link DayOfWeek} object.
     */
    private DayOfWeek fromStringToDayOfWeek(String dayOfWeek) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E");
        TemporalAccessor accessor = formatter.parse(dayOfWeek);
        return DayOfWeek.from(accessor);
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns <code>null</code> if parameter is
     * not found or {@link RPAServicesAccessor} is not defined.
     */
    private String getConfigParam(String key) {
        String result = null;

        if (rpaServices == null) {
            return null;
        }

        try {
            result = rpaServices.getConfigParam(key);
        } catch (Exception e) {
            //do nothing
        }

        return result;
    }
}


