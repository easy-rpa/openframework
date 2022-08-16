package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BusinessCalendar {

    @Inject
    private HolidayRepository calendarRepository;

    public LocalDate addWorkingDays(LocalDate startDate, int numberOfWorkingDaysToAdd){

        for(int i = 0; i < numberOfWorkingDaysToAdd;i++){
            if(isWorkingDay(startDate)){
                startDate = startDate.plusDays(1);
            }
        }
        return startDate;
    }

    public int countWorkingDaysInRange(LocalDate startDate, LocalDate endDate){
        int result = 0;
        while (!startDate.isEqual(endDate)){
            if(isWorkingDay(startDate)){
                result++;
            }
           startDate = startDate.plusDays(1);
        }
        return result;
    }

    public List<LocalDate> getOtherHolidaysInRange(LocalDate startDate, LocalDate endDate){
        List<HolidayEntity> holidays = calendarRepository.findAllOtherHolidays_("dsName");
        List<LocalDate> result = new ArrayList<>();

        for(HolidayEntity holiday: holidays){
            LocalDate holidayDate = LocalDate.of(holiday.getYear(), holiday.getMonth(), holiday.getDay());
            if(holidayDate.isAfter(startDate) && holidayDate.isBefore(endDate)){
                result.add(holidayDate);
            }
        }

        return result;
    }

    public List<LocalDate> getPublicHolidaysInRange(LocalDate startDate, LocalDate endDate){
        return null;
    }

    public boolean isWorkingDay(LocalDate date){

        return true;
    }
}
