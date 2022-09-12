package eu.easyrpa.openframework.calendar.test;

import eu.easyrpa.openframework.calendar.BizCalendar;
import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Test task")
public class TestTask extends ApTask {

    @Inject
    private HolidayRepository holidayRepository;


    private  BizCalendar calendar;

    @Override
    public void execute(){
//        HolidayEntity h1 = new HolidayEntity("USA","USA", HolidayEntity.HolidayType.FIXED,"",
//                2,13,"My Holiday", false,false,
//                HolidayEntity.ChurchHolidayType.NONE,2022,2022, false);
//        holidayRepository.save_(h1);

        calendar = new BizCalendar(holidayRepository,"Belarus");

        LocalDate dateExample = LocalDate.of(2022,11,30);
        System.out.println(calendar.isPublicHoliday(dateExample));
        System.out.println(calendar.isOtherHoliday(dateExample));

        System.out.println(calendar.isWeekend(dateExample));
        System.out.println(calendar.isWorkingDay(dateExample));

       countWorkingDaysInRangeTest(dateExample);
       addWorkingDaysTest(dateExample);
//       getMethodTest();
//       getOtherHolidaysInRangeTest(dateExample);
//       getPublicHolidaysInRangeTest(dateExample);
       getWorkingDaysInRangeTest(dateExample);



    }

    private void countWorkingDaysInRangeTest(LocalDate dateExample){
        System.out.println(calendar.countWorkingDaysInRange(dateExample,dateExample.plusDays(10)));
        System.out.println(calendar.countWorkingDaysInRange(dateExample,dateExample));
        System.out.println(calendar.countWorkingDaysInRange(dateExample, dateExample.minusDays(2)));
    }

    private void addWorkingDaysTest(LocalDate dateExample){
        System.out.println(calendar.addWorkingDays(dateExample,-2));
        System.out.println(calendar.addWorkingDays(dateExample,7));
        System.out.println(calendar.addWorkingDays(dateExample,0));
        System.out.println(calendar.addWorkingDays(dateExample,1));
    }

    private void getMethodTest(){
        System.out.println(calendar.getPublicHolidays());
        System.out.println(calendar.getOtherHolidays());
        System.out.println(calendar.getAllHolidays());
    }

    private void getOtherHolidaysInRangeTest(LocalDate dateExample){
        System.out.println(calendar.getOtherHolidaysInRange(dateExample, dateExample.plusDays(10)));
        System.out.println(calendar.getOtherHolidaysInRange(dateExample, dateExample.plusDays(0)));
        System.out.println(calendar.getOtherHolidaysInRange(dateExample, dateExample.plusDays(-2)));
        System.out.println(calendar.getOtherHolidaysInRange(dateExample, dateExample.plusDays(-1)));
        System.out.println(calendar.getOtherHolidaysInRange(dateExample, dateExample.plusDays(1)));
    }

    private void getPublicHolidaysInRangeTest(LocalDate dateExample){
        System.out.println(calendar.getPublicHolidaysInRange(dateExample, dateExample.plusDays(10)));
        System.out.println(calendar.getPublicHolidaysInRange(dateExample, dateExample.plusDays(0)));
        System.out.println(calendar.getPublicHolidaysInRange(dateExample, dateExample.plusDays(-2)));
        System.out.println(calendar.getPublicHolidaysInRange(dateExample, dateExample.plusDays(-1)));
        System.out.println(calendar.getPublicHolidaysInRange(dateExample, dateExample.plusDays(1)));
    }

    private void getWorkingDaysInRangeTest(LocalDate dateExample) {
        System.out.println(calendar.getWorkingDaysInRange(dateExample,dateExample.plusDays(10)));
        System.out.println(calendar.getWorkingDaysInRange(dateExample,dateExample.plusDays(0)));
        System.out.println(calendar.getWorkingDaysInRange(dateExample,dateExample.plusDays(-2)));
        System.out.println(calendar.getWorkingDaysInRange(dateExample,dateExample.plusDays(-1)));
        System.out.println(calendar.getWorkingDaysInRange(dateExample,dateExample.plusDays(1)));
    }


}
