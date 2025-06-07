package UI.CalendarUI.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import GoogleCalendar.util.JsonUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class EventService extends GoogleCalendarServiceImp{

    public EventService() throws Exception {
        super();
        fetchAndSaveEvents();
    }

    public List<Event> getEventsOnDate(LocalDate date) {
        return getEventsForDate(date);
    }

    @Override
    public List<Event> getEventsForDate(LocalDate date) {
        try {
            List<Event> all = JsonUtil.loadEventsFromJson("src/main/resources/events.json");
            return all.stream()
                    .filter(e -> eventToLocalDate(e).equals(date))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    private LocalDate eventToLocalDate(Event e) {
        DateTime dt = e.getStart().getDateTime();
        if (dt == null) {
            dt = e.getStart().getDate();
        }
        return Instant.ofEpochMilli(dt.getValue())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /*public void addEvent(String title, String description, LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String start = date.atStartOfDay().format(fmt);
        String end = date.plusDays(1).atStartOfDay().format(fmt);

        createEvent(title, "", description, start, end);
    }*/
}
