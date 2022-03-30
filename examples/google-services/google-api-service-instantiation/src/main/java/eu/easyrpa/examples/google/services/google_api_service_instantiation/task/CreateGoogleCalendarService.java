package eu.easyrpa.examples.google.services.google_api_service_instantiation.task;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import eu.easyrpa.openframework.google.services.GoogleServicesProvider;
import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Create Google Calendar Service")
public class CreateGoogleCalendarService extends ApTask {

    private static final String CALENDAR_ID = "primary";
    private static final String TIME_ZONE = "Europe/Zurich";

    @Inject
    private GoogleServicesProvider googleServicesProvider;

    private Calendar calendar;

    @AfterInit
    public void init() {
        calendar = googleServicesProvider.getService(Calendar.class, CalendarScopes.CALENDAR_EVENTS);
        log.info("Calendar instance has been created");
    }

    public void execute() throws IOException {
        createPrimaryCalendarEvent("Example event", "Lenina 7, Minsk, Belarus",
                "2022-04-28T09:00:00-07:00", "2022-04-28T17:00:00-07:00");
        Events events = calendar.events().list(CALENDAR_ID).execute();
        List<Event> eventList = events.getItems();
        eventList.forEach(event -> log.info("Summary: '{}' Location: '{}' Description: '{}'", event.getSummary(),
                event.getLocation(), event.getDescription()));
    }

    private void createPrimaryCalendarEvent(String summary,
                                            String location,
                                            String startEventDate,
                                            String endEventDate) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location);
        setPrimaryCalendarEventDate(event, startEventDate, endEventDate);
        event = calendar.events().insert(CALENDAR_ID, event).execute();
        log.info("Event created '{}'", event.getHtmlLink());
    }

    private void setPrimaryCalendarEventDate(Event event, String startEventDate, String endEventDate) {
        DateTime startDateTime = new DateTime(startEventDate);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(TIME_ZONE);
        event.setStart(start);
        DateTime endDateTime = new DateTime(endEventDate);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(TIME_ZONE);
        event.setEnd(end);
        log.info("Event time has been set. Start date: '{}' End date: '{}'", start.getDateTime(), end.getDateTime());
    }
}