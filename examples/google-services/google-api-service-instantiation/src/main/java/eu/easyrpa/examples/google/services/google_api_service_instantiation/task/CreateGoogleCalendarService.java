package eu.easyrpa.examples.google.services.google_api_service_instantiation.task;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import eu.easyrpa.openframework.google.services.GoogleServicesProvider;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Create Google Calendar Service")
public class CreateGoogleCalendarService extends ApTask {

    @Inject
    private GoogleServicesProvider googleServicesProvider;

    public void execute() {

        Calendar calendar = googleServicesProvider.getService(Calendar.class, CalendarScopes.CALENDAR_EVENTS);

        //TODO get list of events or create a new event
    }
}