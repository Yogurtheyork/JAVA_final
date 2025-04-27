package UI.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;

public class CalendarEvent {

    //initialize variables
    private final String title;
    private final LocalDate date;
    private final String start;
    private final String end;
    private final String description;
    private String googleCalendarId;

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public CalendarEvent(String title, LocalDate date, String start, String end, String description) {
        this.title = title;
        this.date = date;
        this.start = start;
        this.end = end;
        this.description = description;
        this.googleCalendarId = "";
    }

    // By York, put an error exception here might be better
    public JSONObject toJSON() {
        JSONObject eventJson = new JSONObject();
        eventJson.put("title", title);
        eventJson.put("date", date.format(DATE_FORMAT));
        eventJson.put("start", start);
        eventJson.put("end", end);
        eventJson.put("description", description);
        eventJson.put("googleCalendarId", googleCalendarId);
        return eventJson;
    }

    public static CalendarEvent fromJSON(JSONObject jsonObject) {
        String title = (String) jsonObject.get("title");
        LocalDate date = LocalDate.parse((String) jsonObject.get("date"), DATE_FORMAT);
        String start = (String) jsonObject.get("start");
        String end = (String) jsonObject.get("end");
        String description = (String) jsonObject.get("description");

        CalendarEvent event = new CalendarEvent(title, date, start, end, description);
        String googleId = (String) jsonObject.get("googleCalendarId");
        if (googleId != null) {
            event.googleCalendarId = googleId;
        }
        return event;
    }

    // Getters
    public String getTitle() { return title; }
    public LocalDate getDate() { return date; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public String getDescription() { return description; }
    public String getGoogleCalendarId() { return googleCalendarId; }

    // Setters
    public void setGoogleCalendarId(String id) { this.googleCalendarId = id; }
} 