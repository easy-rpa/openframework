package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;

import java.time.LocalDate;
import java.util.*;

public class BizCalendar {

    //date from BP format : dd-mm-YYYY

    private final List<HolidayEntity> holidayEntities;
    private final List<HolidayEntity> otherHolidays;
    private final List<HolidayEntity> publicHolidays;
    private final Calendar calendar;

    public BizCalendar(List<HolidayEntity> holidayEntities, HolidayRepository holidayRepository) {
        this.holidayEntities = holidayEntities;
        calendar = new GregorianCalendar(Locale.forLanguageTag(holidayEntities.get(0).getRegion()));
        otherHolidays = holidayRepository.findAllOtherHolidays_("*COMMON_DS_NAME*");
        publicHolidays = holidayRepository.findAllPublicHolidays_("*COMMON_DS_NAME*");
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

        while(!startDate.isEqual(endDate)) {
            if(isWorkingDay(startDate)){
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
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY;

    }

    public boolean isWorkingDay(LocalDate date) {
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            return false;
        } else {
            for (HolidayEntity holidayEntity : holidayEntities) {
                if (holidayEntity.getMonth() == date.getMonthValue()
                        && holidayEntity.getDay() == date.getDayOfMonth()) {
                    return false;
                }
            }
        }
        return true;
    }

    //TODO: Simplify this method
//    private List<HolidayEntity> findHolidaysInRange(List<HolidayEntity> holidayEntities, LocalDate startDate, LocalDate endDate) {
//        List<HolidayEntity> result = new ArrayList<>();
//        if (startDate.getYear() == endDate.getYear()) {
//            for (HolidayEntity holiday : holidayEntities) {
//                if (startDate.getMonthValue() == holiday.getMonth() && endDate.getMonthValue() == startDate.getMonthValue()) {
//                    if (startDate.getDayOfMonth() < holiday.getDay() && endDate.getDayOfMonth() > holiday.getDay()) {
//                        result.add(holiday);
//                    }
//                }
//
//                if (startDate.getMonthValue() == holiday.getMonth() && endDate.getMonthValue() != holiday.getMonth()) {
//                    if (startDate.getDayOfMonth() < holiday.getDay()) {
//                        result.add(holiday);
//                    }
//                }
//
//                if (endDate.getMonthValue() == holiday.getMonth() && startDate.getMonthValue() != holiday.getMonth()) {
//                    if (endDate.getDayOfMonth() > holiday.getDay()) {
//                        result.add(holiday);
//                    }
//                }
//
//                if (startDate.getMonthValue() < holiday.getMonth() && endDate.getMonthValue() > holiday.getMonth()) {
//                    result.add(holiday);
//                }
//            }
//        }
//
//        if (startDate.getYear() == endDate.getYear() + 1) {
//            for (HolidayEntity holiday : holidayEntities) {
//                if (startDate.getMonthValue() == holiday.getMonth() && endDate.getMonthValue() != holiday.getMonth()) {
//                    if (startDate.getDayOfMonth() < holiday.getDay()) {
//                        result.add(holiday);
//                    }
//                }
//
//                if (endDate.getMonthValue() == holiday.getMonth() && startDate.getMonthValue() != holiday.getMonth()) {
//                    if (endDate.getDayOfMonth() > holiday.getDay()) {
//                        result.add(holiday);
//                    }
//                }
//
//                if (startDate.getMonthValue() < holiday.getMonth() || endDate.getMonthValue() > holiday.getMonth()) {
//                    result.add(holiday);
//                }
//            }
//        }
//        return result;
//    }

    private List<HolidayEntity> findHolidaysInRange(List<HolidayEntity> holidayEntities, LocalDate startDate, LocalDate endDate){
        List<HolidayEntity> result = new ArrayList<>();

        while(!startDate.isEqual(endDate)){
            if(isOtherHoliday(startDate)){
                for(HolidayEntity holidayEntity: holidayEntities){
                    if(startDate.getMonthValue()==holidayEntity.getMonth() && startDate.getDayOfMonth() == holidayEntity.getDay()){
                        result.add(holidayEntity);
                      startDate =  startDate.plusDays(1);
                    }
                }
            }
        }

        return result;
    }

}


