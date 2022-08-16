package eu.easyrpa.openframework.calendar;

import eu.easyrpa.openframework.calendar.entity.Holiday;
import eu.easyrpa.openframework.calendar.repository.CalendarRepository;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Test data store task")
public class TestTask extends ApTask {

    @Inject
    private CalendarRepository calendarRepository;

    @Override
    public void execute() throws Exception {
        Holiday holiday = new Holiday("BRuh", 2,2,false);
        calendarRepository.save(holiday);

        System.out.println(calendarRepository.getHolidayByName("BRuh"));
    }
}
