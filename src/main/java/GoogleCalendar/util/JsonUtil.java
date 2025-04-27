package GoogleCalendar.util;

import com.google.api.services.calendar.model.Event;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveEventsToJson(List<Event> events, String filePath) throws IOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(events, writer);
        }
    }

    public static List<Event> loadEventsFromJson(String filePath) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, new TypeToken<List<Event>>(){}.getType());
        }
    }

    public static String eventToJson(Event event) {
        return gson.toJson(event);
    }

    public static Event jsonToEvent(String json) {
        return gson.fromJson(json, Event.class);
    }
} 