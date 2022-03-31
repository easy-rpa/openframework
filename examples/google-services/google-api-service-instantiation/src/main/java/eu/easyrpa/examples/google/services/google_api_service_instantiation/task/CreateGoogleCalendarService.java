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
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Create Google Calendar Service")
public class CreateGoogleCalendarService extends ApTask {

    @Inject
    private GoogleServicesProvider googleServicesProvider;

    private Calendar calendar;

    @Configuration(value = "google.primary.calendar.id")
    private String primaryCalendarId;

    @Configuration(value = "google.holiday.calendar.id")
    private String holidayCalendarId;

    @AfterInit
    public void init() {
        calendar = googleServicesProvider.getService(Calendar.class, CalendarScopes.CALENDAR);
    }

    public void execute() throws IOException {
        String dateOfMeeting = "2022-05-09";
        boolean isHoliday = isPublicHoliday(DateTime.parseRfc3339(dateOfMeeting));
        if(!isHoliday) {
            createPrimaryCalendarEvent("Meeting of Founders", "Lenina 7, Minsk, Belarus", dateOfMeeting,
                    "Founders meeting. Please arrive exactly on time. No delays. Take quarterly reports with you.");
        }
    }

    private void createPrimaryCalendarEvent(String summary,
                                            String location,
                                            String dateEvent,
                                            String description) throws IOException {
        Event event = new Event().setSummary(summary).setLocation(location).setDescription(description);
        log.info("Set summary, location, description of event '{}', '{}', '{}'", summary, location, description);
        EventDateTime eventDateTime = new EventDateTime().setDate(new DateTime(dateEvent)).setTimeZone(null);
        event.setStart(eventDateTime).setEnd(eventDateTime);
        log.info("Event date time is '{}'", event.getStart());
        event = calendar.events().insert(primaryCalendarId, event).execute();
        log.info("Event created '{}'", event.getHtmlLink());
    }

    private boolean isPublicHoliday(DateTime publicDayTime) throws IOException {
        boolean isHoliday = false;
        Events events = calendar.events().list(holidayCalendarId).execute();
        log.info("Obtain Holiday Calendar");
        List<Event> eventList = events.getItems();
        log.info("Got all events in Holiday Calendar");
        for (Event event : eventList) {
            if (event.getStart().getDate().toStringRfc3339().equals(publicDayTime.toStringRfc3339())) {
                isHoliday = true;
                log.info("This day is a Public Holiday '{}'", event.getSummary());
            }
        }
        return isHoliday;
    }
}