package eu.easyrpa.examples.google.services.google_api_service_instantiation.task;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import eu.easyrpa.openframework.google.services.GoogleServicesProvider;
import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Create Google Calendar Service")
public class CreateGoogleCalendarService extends ApTask {

    @Inject
    private GoogleServicesProvider googleServicesProvider;

    private Calendar calendar;

    @Configuration(value = "google.holiday.calendar.id")
    private String holidayCalendarId;

    @AfterInit
    public void init() {
        calendar = googleServicesProvider.getService(Calendar.class, CalendarScopes.CALENDAR);
    }

    public void execute() throws IOException {
        if (isNationalHoliday(LocalDate.now())) {
            log.info("Today is holiday");
        } else {
            log.info("Today is ordinary day");
        }
    }

    private boolean isNationalHoliday(LocalDate date) throws IOException {
        boolean isNationalHoliday = false;
        Events events = calendar.events().list(holidayCalendarId).execute();
        List<Event> eventList = events.getItems();
        for (Event event : eventList) {
            if (event.getStart().getDate().getValue() == date
                    .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()) {
                isNationalHoliday = true;
            }
        }
        return isNationalHoliday;
    }
}