package UI.CalendarUI.service;

import com.google.api.services.calendar.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface GoogleCalendarService {

    List<Event> fetchEvents() throws Exception;

    void uploadEvents(List<Event> events) throws Exception;

    void deleteEvent(String eventId) throws Exception;

    void updateEvent(Event event) throws Exception;

    void insertEvent(Event event)throws Exception;

    Event createEvent(String summary, String location, String description,
                     String startTime, String endTime);

    List<Event> getEventsForDate(LocalDate date);
}