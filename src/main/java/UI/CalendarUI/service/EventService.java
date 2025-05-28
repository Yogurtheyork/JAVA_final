package UI.service;

import UI.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventService {

    private final GoogleCalendarService googleService;

    public EventService(GoogleCalendarService googleService) {
        this.googleService = googleService;
    }

    public List<Event> getEventsOnDate(LocalDate date) {
        return googleService.getEventsForDate(date);
    }

    public void addEvent(String title, String description, LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String start = date.atStartOfDay().format(fmt);
        String end = date.plusDays(1).atStartOfDay().format(fmt);
        String timeZone = "Asia/Taipei";
        googleService.createEvent(title, description, start, end, timeZone);
    }
}
