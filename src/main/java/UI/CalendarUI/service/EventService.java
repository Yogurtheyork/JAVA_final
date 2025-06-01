package UI.CalendarUI.service;

import UI.CalendarUI.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventService extends GoogleCalendarServiceImp{

    public EventService() throws Exception {
        super();
        fetchEvents();
    }

    public List<Event> getEventsOnDate(LocalDate date) {
        return getEventsForDate(date);
    }

    /*public void addEvent(String title, String description, LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String start = date.atStartOfDay().format(fmt);
        String end = date.plusDays(1).atStartOfDay().format(fmt);

        createEvent(title, "", description, start, end);
    }*/
}
