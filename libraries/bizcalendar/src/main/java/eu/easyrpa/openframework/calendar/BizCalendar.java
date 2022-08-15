package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;

import java.time.LocalDate;
import java.util.*;

public class BizCalendar {

    //date from BP format : dd-mm-YYYY

    private List<HolidayEntity> holidayEntities;


    private Calendar calendar;

    public BizCalendar(List<HolidayEntity> holidayEntities) {
        this.holidayEntities = holidayEntities;
        calendar = new GregorianCalendar(Locale.forLanguageTag(holidayEntities.get(0).getRegion()));
    }

//
//    @AfterInit
//    public void init() {
//        calendar = new GregorianCalendar(Locale.forLanguageTag(holidays.get(0).getRegion()));
//    }

    public void addWorkingDays() {

    }

    public void countWorkingDaysInRange() {

    }

    public void getOtherHolidaysInRange() {

    }

    public void getPublicHolidaysInRange() {

    }

    public void getWorkingDaysInRange() {

    }

    public void isOtherHoliday(LocalDate date) {

    }

    public void isPublicHoliday(LocalDate date) {
        
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
                if(holidayEntity.getMonth() == date.getMonthValue()
                        && holidayEntity.getDay() == date.getDayOfMonth()) {
                    return false;
                }
            }
        }
        return true;
    }

}
