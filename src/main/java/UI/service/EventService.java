package UI.service;

import UI.model.CalendarEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventService {
    private static final String EVENT_FILE = "src/main/resources/calendar_events.json";
    private List<CalendarEvent> events;

    public EventService() {
        this.events = new ArrayList<>();
        loadEvents();
    }

    // Take events from calendar_events.json
    public void loadEvents() {
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(EVENT_FILE);
            JSONArray eventsArray = (JSONArray) parser.parse(reader);
            reader.close();

            events.clear();
            for (Object obj : eventsArray) {
                JSONObject eventJson = (JSONObject) obj;
                events.add(CalendarEvent.fromJSON(eventJson));
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error loading events: " + e.getMessage());
            events = new ArrayList<>();
        }
    }

    public void saveEvents() {
        try {
            JSONArray eventsArray = new JSONArray();
            for (CalendarEvent event : events) {
                eventsArray.add(event.toJSON());
            }

            FileWriter writer = new FileWriter(EVENT_FILE);
            writer.write(eventsArray.toJSONString());
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving events: " + e.getMessage());
        }
    }

    public void addEvent(CalendarEvent event) {
        events.add(event);
        saveEvents();
    }

    public void updateEvent(CalendarEvent oldEvent, CalendarEvent newEvent) {
        int index = events.indexOf(oldEvent);
        if (index != -1) {
            events.set(index, newEvent);
            saveEvents();
        }
    }

    public void deleteEvent(CalendarEvent event) {
        events.remove(event);
        saveEvents();
    }

    public List<CalendarEvent> getEventsForDate(LocalDate date) {
        List<CalendarEvent> dayEvents = new ArrayList<>();
        for (CalendarEvent event : events) {
            if (event.getDate().equals(date)) {
                dayEvents.add(event);
            }
        }
        return dayEvents;
    }

    public int countEventsInMonth(int year, int month) {
        int count = 0;
        for (CalendarEvent event : events) {
            LocalDate date = event.getDate();
            if (date.getYear() == year && date.getMonthValue() == month) {
                count++;
            }
        }
        return count;
    }

    public List<CalendarEvent> getAllEvents() {
        return new ArrayList<>(events);
    }
} 