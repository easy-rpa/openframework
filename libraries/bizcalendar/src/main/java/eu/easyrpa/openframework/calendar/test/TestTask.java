package eu.easyrpa.openframework.calendar.test;

import eu.easyrpa.openframework.calendar.BizCalendar;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.LocalDate;

@Slf4j
@ApTaskEntry(name = "Test task")
public class TestTask extends ApTask {

    @Inject
    private HolidayRepository holidayRepository;

    private  BizCalendar calendar;

    @Override
    public void execute(){
//        HolidayEntity h1 = new HolidayEntity("USA","USA", HolidayEntity.HolidayType.FLOATING,"",
//                5,5,"My Holiday", true,false,
//                HolidayEntity.ChurchHolidayType.ISLAMIC,0, 1444,1444, false);
//        HolidayEntity h2 = new HolidayEntity("USA","USA", HolidayEntity.HolidayType.FIXED,"",
//                6,5,"My Holiday", false,true,
//                HolidayEntity.ChurchHolidayType.NONE,0, 2022,2022, false);
//        HolidayEntity h3 = new HolidayEntity("USA","USA", HolidayEntity.HolidayType.FLOATING,"3 THU MAY",
//                5,0,"My Holiday", false,false,
//                HolidayEntity.ChurchHolidayType.NONE,0, 2022,2023, false);
//        HolidayEntity h4 = new HolidayEntity("USA","USA", HolidayEntity.HolidayType.FLOATING,"",
//                0,0,"My Holiday", true,false,
//                HolidayEntity.ChurchHolidayType.ORTHODOX,9, 2022,2022, false);
//        HolidayEntity h5 = new HolidayEntity("USA","USA", HolidayEntity.HolidayType.FIXED,"",
//                2,14,"My Holiday", false,true,
//                HolidayEntity.ChurchHolidayType.NONE,0, 2022,2022, false);
//        HolidayEntity h6 = new HolidayEntity("USA","USA", HolidayEntity.HolidayType.FIXED,"",
//                9,3,"My Holiday", false,false,
//                HolidayEntity.ChurchHolidayType.NONE,0, 2022,2022, true);
//
//        holidayRepository.save_(h1);
//        holidayRepository.save_(h2);
//        holidayRepository.save_(h3);
//        holidayRepository.save_(h4);
//        holidayRepository.save_(h5);
//        holidayRepository.save_(h6);
        calendar = new BizCalendar(holidayRepository);

        System.out.println(calendar.isWeekend(LocalDate.of(2022,9,18)));
        System.out.println(calendar.isWeekend(LocalDate.of(2022,9,17)));
        System.out.println(calendar.isWeekend(LocalDate.now()));
        LocalDate date1 = LocalDate.of(2022,5,19);
        LocalDate date2 = LocalDate.of(2022,5,3);
        LocalDate date3 = LocalDate.of(2022,10,5);
        LocalDate date4 = LocalDate.of(2023, 2,14);
        LocalDate date5 = LocalDate.of(2022, 10,29);
        LocalDate date6 = LocalDate.of(2023, 9,3);
        LocalDate date7 = LocalDate.of(2022,12,2);
        LocalDate date8 = LocalDate.of(2022,6,5);

        System.out.println(calendar.isWorkingDay(date1));
        System.out.println(calendar.isWorkingDay(date2));
        System.out.println(calendar.isWorkingDay(date3));
        System.out.println(calendar.isWorkingDay(date4));
        System.out.println(calendar.isWorkingDay(date5));
        System.out.println(calendar.isWorkingDay(date6));
        System.out.println(calendar.isWorkingDay(date7));
        System.out.println(calendar.isWorkingDay(date8));
        System.out.println();
        System.out.println(calendar.isOtherHoliday(date1));
        System.out.println(calendar.isOtherHoliday(date2));
        System.out.println(calendar.isOtherHoliday(date3));
        System.out.println(calendar.isOtherHoliday(date4));
        System.out.println(calendar.isOtherHoliday(date5));
        System.out.println(calendar.isOtherHoliday(date6));
        System.out.println(calendar.isOtherHoliday(date7));
        System.out.println(calendar.isOtherHoliday(date8));
        System.out.println();
        System.out.println(calendar.isPublicHoliday(date1));
        System.out.println(calendar.isPublicHoliday(date2));
        System.out.println(calendar.isPublicHoliday(date3));
        System.out.println(calendar.isPublicHoliday(date4));
        System.out.println(calendar.isPublicHoliday(date5));
        System.out.println(calendar.isPublicHoliday(date6));
        System.out.println(calendar.isPublicHoliday(date7));
        System.out.println(calendar.isPublicHoliday(date8));
        System.out.println();

        addWorkingDaysTest(date1);
        getMethodTest();

        System.out.println(calendar.countWorkingDaysInRange(LocalDate.of(2022,5,2), LocalDate.of(2022,5,30)));
        System.out.println(calendar.getWorkingDaysInRange(LocalDate.of(2022,5,1), LocalDate.of(2022,5,30)));
        System.out.println(calendar.getPublicHolidaysInRange(LocalDate.of(2022,5,1), LocalDate.of(2022,5,30)));
        System.out.println(calendar.getOtherHolidaysInRange(LocalDate.of(2022,5,1), LocalDate.of(2022,5,30)));


    }

    private void countWorkingDaysInRangeTest(LocalDate dateExample){
        System.out.println(calendar.countWorkingDaysInRange(dateExample,dateExample.plusDays(10)));
        System.out.println(calendar.countWorkingDaysInRange(dateExample,dateExample));
        System.out.println(calendar.countWorkingDaysInRange(dateExample, dateExample.minusDays(2)));
    }

    private void addWorkingDaysTest(LocalDate dateExample){
        System.out.println(calendar.addWorkingDays(dateExample,-2));
        System.out.println(calendar.addWorkingDays(dateExample,7));
        System.out.println(calendar.addWorkingDays(dateExample,-7));
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
