package GoogleCalendar.service;

import com.google.api.services.calendar.model.Event;

import java.time.ZonedDateTime;
import java.util.List;

public interface GoogleCalendarService {

    List<Event> fetchEvents() throws Exception;

    void uploadEvents(List<Event> events) throws Exception;

    void deleteEvent(String eventId) throws Exception;

    void updateEvent(Event event) throws Exception;

    void insertEvent(Event event)throws Exception;

    Event createEvent(String summary, String location, String description,
                      ZonedDateTime S, ZonedDateTime E);
}