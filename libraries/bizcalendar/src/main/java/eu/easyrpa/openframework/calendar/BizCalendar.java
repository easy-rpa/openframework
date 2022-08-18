package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BizCalendar {

    //date from BP format : dd-mm-YYYY

    private final List<HolidayEntity> holidayEntities;
    private final List<HolidayEntity> otherHolidays;
    private final List<HolidayEntity> publicHolidays;

    public BizCalendar(List<HolidayEntity> holidayEntities, HolidayRepository holidayRepository) {
        this.holidayEntities = holidayEntities;
        this.otherHolidays = holidayRepository.findAllOtherHolidays_("*COMMON_DS_NAME*");
        this.publicHolidays = holidayRepository.findAllPublicHolidays_("*COMMON_DS_NAME*");
    }

    public LocalDate addWorkingDays(LocalDate startDate, int numberOfWorkingDaysToAdd) {
        for (int i = 0; i < numberOfWorkingDaysToAdd; i++) {
            if (isWorkingDay(startDate)) {
                startDate = startDate.plusDays(1);
            } else {
                startDate = startDate.plusDays(1);
                numberOfWorkingDaysToAdd++;
            }

        }
        return startDate;
    }

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

    public List<HolidayEntity> getOtherHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return null;
        }

        return findHolidaysInRange(otherHolidays, startDate, endDate);
    }

    public List<HolidayEntity> getPublicHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return null;
        }

        return findHolidaysInRange(publicHolidays, startDate, endDate);
    }

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

    public boolean isOtherHoliday(LocalDate date) {
        for (HolidayEntity holiday : otherHolidays) {
            if (date.getMonthValue() == holiday.getMonth() && date.getDayOfMonth() == holiday.getDay()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublicHoliday(LocalDate date) {
        for (HolidayEntity holiday : publicHolidays) {
            if (date.getMonthValue() == holiday.getMonth() && date.getDayOfMonth() == holiday.getDay()) {
                return true;
            }
        }
        return false;
    }

    public boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SUNDAY && date.getDayOfWeek() != DayOfWeek.SATURDAY;
    }


    private List<HolidayEntity> findHolidaysInRange(List<HolidayEntity> holidayEntities, LocalDate startDate, LocalDate endDate) {
        List<HolidayEntity> result = new ArrayList<>();

        while (!startDate.isEqual(endDate)) {
            if (isOtherHoliday(startDate)) {
                for (HolidayEntity holidayEntity : holidayEntities) {
                    if (startDate.getMonthValue() == holidayEntity.getMonth() && startDate.getDayOfMonth() == holidayEntity.getDay()) {
                        result.add(holidayEntity);
                        startDate = startDate.plusDays(1);
                    }
                }
            }
        }

        return result;
    }

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

    public boolean isWorkingDay(LocalDate date) {
        if (isWeekend(date)) {
            return false;
        } else {
            for (HolidayEntity holidayEntity : holidayEntities) {
                if (holidayEntity.getMonth() == date.getMonthValue()
                        && holidayEntity.getDay() == date.getDayOfMonth()) {
                    if (holidayEntity.isSubstitute()) {
                        LocalDate weekendDate = LocalDate.of(date.getYear(), date.getMonthValue() , date.getDayOfMonth());
                        while (!isWeekend(weekendDate)) {
                            weekendDate = weekendDate.plusDays(1);
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

}
