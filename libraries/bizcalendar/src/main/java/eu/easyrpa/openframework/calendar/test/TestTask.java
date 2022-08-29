package eu.easyrpa.openframework.calendar.test;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.easyrpa.openframework.calendar.repository.HolidayRepository;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Test task")
public class TestTask extends ApTask {

    @Inject
    private HolidayRepository holidayRepository;

    @Override
    public void execute() throws Exception {
        HolidayEntity holidayEntity = new HolidayEntity("Belarus","BY", HolidayEntity.HolidayType.FIXED,4,6,"Special Day",false,true,"Islamic",2022,2024,false);
        holidayRepository.save_(holidayEntity);

        System.out.println( holidayRepository.findAll_("Belarus"));
    }

}
