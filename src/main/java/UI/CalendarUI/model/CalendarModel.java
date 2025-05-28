package UI.model;

import com.google.api.services.calendar.model.Event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarModel {

    private final Map<LocalDate, List<Event>> eventMap;

    public CalendarModel() {
        eventMap = new HashMap<>();
    }

    public void setEventsForDate(LocalDate date, List<Event> events) {
        eventMap.put(date, events);
    }

    public void addEvent(LocalDate date, Event event) {
        eventMap.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
    }

    public List<Event> getEventsOnDate(LocalDate date) {
        return eventMap.getOrDefault(date, new ArrayList<>());
    }

    public void clearAllEvents() {
        eventMap.clear();
    }
}
