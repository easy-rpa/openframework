package eu.easyrpa.openframework.calendar.test;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.CalendarRepo;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Test task")
public class TestTask extends ApTask {

    @Inject
    private CalendarRepo calendarRepo;

    @Inject
    private HolidayRepository holidayRepository;

    @Override
    public void execute() throws Exception {
        // HolidayEntity holidayEntity = new HolidayEntity("Belarus","BY", HolidayEntity.HolidayType.FIXED,4,6,"Special Day",false,true,"Islamic",2022,2024,false);

        HolidayEntity holiday = new HolidayEntity("Belarus", "BY", HolidayEntity.HolidayType.FIXED,
                "", 12, 1, "Bruh", false, true,
                HolidayEntity.ChurchHolidayType.NONE, 2022, 2022, false);

        holidayRepository.save(holiday);

        System.out.println(holidayRepository.findAll());
    }
}
