package eu.easyrpa.openframework.calendar.test;

import eu.easyrpa.openframework.calendar.BizCalendar;
import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
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

    @Override
    public void execute(){

        HolidayEntity holiday = new HolidayEntity("Belarus", "BY", HolidayEntity.HolidayType.FIXED,
                "", 12, 1, "Bruh", false, true,
                HolidayEntity.ChurchHolidayType.NONE, 2022, 2022, false);
        holidayRepository.save(holiday);
        System.out.println(holidayRepository.findAll());

        LocalDate dateExample = LocalDate.of(2022,9,4);



        BizCalendar calendar = new BizCalendar(holidayRepository,"belarus");

        System.out.println(calendar.isPublicHoliday(dateExample));
        System.out.println(calendar.isOtherHoliday(dateExample));
        System.out.println(calendar.isWeekend(dateExample));
        System.out.println(calendar.isWorkingDay(dateExample));

        System.out.println(calendar.addWorkingDays(dateExample,7));
        System.out.println(calendar.countWorkingDaysInRange(dateExample,dateExample.plusDays(10)));
        System.out.println(calendar.getOtherHolidaysInRange(dateExample, dateExample.plusDays(80)));
        System.out.println(calendar.getWorkingDaysInRange(dateExample, dateExample.plusDays(80)));
        System.out.println(calendar.getPublicHolidaysInRange(dateExample, dateExample.plusDays(80)));

    }
}
