package UI.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import java.time.format.DateTimeParseException;
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
        String title = jsonObject.containsKey("title") ? (String) jsonObject.get("title") : "(未命名)";

        LocalDate date = null;
        if (jsonObject.containsKey("date")) {
            try {
                date = LocalDate.parse((String) jsonObject.get("date"), DATE_FORMAT);
            } catch (Exception e) {
                System.err.println("日期格式錯誤：" + jsonObject.get("date") + "，跳過解析。");
            }
        } else {
            System.err.println("錯誤：這筆事件缺少 date 欄位 -> " + jsonObject);
        }

        String start = jsonObject.containsKey("start") ? (String) jsonObject.get("start") : "";
        String end = jsonObject.containsKey("end") ? (String) jsonObject.get("end") : "";
        String description = jsonObject.containsKey("description") ? (String) jsonObject.get("description") : "";

        CalendarEvent event = new CalendarEvent(title, date, start, end, description);

        if (jsonObject.containsKey("googleCalendarId")) {
            event.googleCalendarId = (String) jsonObject.get("googleCalendarId");
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