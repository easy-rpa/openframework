package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;

import java.time.LocalDate;
import java.util.*;

public class BizCalendar {

    //date from BP format : dd-mm-YYYY

    private List<HolidayEntity> holidayEntities;


    private Calendar calendar;

    private HolidayRepository holidayRepository;

    public BizCalendar(List<HolidayEntity> holidayEntities) {
        this.holidayEntities = holidayEntities;
        calendar = new GregorianCalendar(Locale.forLanguageTag(holidayEntities.get(0).getRegion()));
    }

//
//    @AfterInit
//    public void init() {
//        calendar = new GregorianCalendar(Locale.forLanguageTag(holidays.get(0).getRegion()));
//    }

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

    //TODO: simplify this method and think about year field in entity
    public List<HolidayEntity> getOtherHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        List<HolidayEntity> result = new ArrayList<>();
        List<HolidayEntity> otherHolidays = holidayRepository.findAllOtherHolidays_("*COMMON_DS_NAME*");
        if (startDate.isAfter(endDate)) {
            return null;
        }

        return findHolidaysInRange(otherHolidays, startDate, endDate);
    }

        public List<HolidayEntity> getPublicHolidaysInRange (LocalDate startDate, LocalDate endDate) {
            List<HolidayEntity> result = new ArrayList<>();
            List<HolidayEntity> otherHolidays = holidayRepository.findAllPublicHolidays_("*COMMON_DS_NAME*");
            if (startDate.isAfter(endDate)) {
                return null;
            }

            return findHolidaysInRange(otherHolidays, startDate, endDate);
        }

        public void getWorkingDaysInRange () {

        }

        public void isOtherHoliday (LocalDate date){

        }

        public void isPublicHoliday(LocalDate date){

        }

        public boolean isWeekend(LocalDate date){
            calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY;

        }

        public boolean isWorkingDay(LocalDate date){
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

        private List<HolidayEntity> findHolidaysInRange(List<HolidayEntity> holidayEntities, LocalDate startDate, LocalDate endDate){
            List<HolidayEntity> result = new ArrayList<>();
            if (startDate.getYear() == endDate.getYear()) {
                for (HolidayEntity holiday : holidayEntities) {
                    if (startDate.getMonthValue() == holiday.getMonth() && endDate.getMonthValue() == startDate.getMonthValue()) {
                        if (startDate.getDayOfMonth() < holiday.getDay() && endDate.getDayOfMonth() > holiday.getDay()) {
                            result.add(holiday);
                        }
                    }

                    if (startDate.getMonthValue() == holiday.getMonth() && endDate.getMonthValue() != holiday.getMonth()) {
                        if (startDate.getDayOfMonth() < holiday.getDay()) {
                            result.add(holiday);
                        }
                    }

                    if (endDate.getMonthValue() == holiday.getMonth() && startDate.getMonthValue() != holiday.getMonth()) {
                        if (endDate.getDayOfMonth() > holiday.getDay()) {
                            result.add(holiday);
                        }
                    }

                    if (startDate.getMonthValue() < holiday.getMonth() && endDate.getMonthValue() > holiday.getMonth()) {
                        result.add(holiday);
                    }
                }
            }

            if (startDate.getYear() == endDate.getYear() + 1) {
                for (HolidayEntity holiday : holidayEntities) {

                    if (startDate.getMonthValue() == holiday.getMonth() && endDate.getMonthValue() != holiday.getMonth()) {
                        if (startDate.getDayOfMonth() < holiday.getDay()) {
                            result.add(holiday);
                        }
                    }

                    if (endDate.getMonthValue() == holiday.getMonth() && startDate.getMonthValue() != holiday.getMonth()) {
                        if (endDate.getDayOfMonth() > holiday.getDay()) {
                            result.add(holiday);
                        }
                    }

                    if (startDate.getMonthValue() < holiday.getMonth() || endDate.getMonthValue() > holiday.getMonth()) {
                        result.add(holiday);
                    }
                }
            }
            return  result;
        }

    }


