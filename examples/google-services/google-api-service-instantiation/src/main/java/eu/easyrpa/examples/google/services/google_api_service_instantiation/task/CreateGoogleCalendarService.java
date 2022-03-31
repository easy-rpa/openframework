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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Create Google Calendar Service")
public class CreateGoogleCalendarService extends ApTask {

    @Inject
    private GoogleServicesProvider googleServicesProvider;

    private Calendar calendar;

    @Configuration(value = "google.primary.calendar.id")
    private String primaryCalendarId;

    @AfterInit
    public void init() {
        calendar = googleServicesProvider.getService(Calendar.class, CalendarScopes.CALENDAR);
    }

    public void execute() throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        if(!isHoliday(currentDate)) {
            log.info("Today is ordinary day '{}'", currentDate);
        }
    }

    private boolean isHoliday(String currentDate) throws IOException {
        boolean isHoliday = false;
        Events events = calendar.events().list(primaryCalendarId).execute();
        List<Event> eventList = events.getItems();
        log.info("Getting all events in primary calendar");
        for (Event event : eventList) {
            String line = event.getStart().getDate().toString();
            if (line.equals(currentDate)) {
                log.info("The holiday is '{}'", event.getSummary());
                isHoliday = true;
            }
        }
        return isHoliday;
    }
}