package UI.CalendarUI.service;

import UI.CalendarUI.service.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class JsonService {

    private static final String EVENTS_FILE_PATH = "src/main/resources/events.json";
    Gson gson;
    FileReader reader;

    public JsonService(){
        gson = new Gson();
        reader = null;
        try {
            reader = new FileReader(EVENTS_FILE_PATH);
        }catch (FileNotFoundException e){
            System.out.println("File events.json was not found.");
        }
    }

    public void getEventInfo(){
        Type listType = new TypeToken<List<Event>>(){}.getType();
        List<Event> ListEvents = gson.fromJson(reader, listType);

        for (Event e  : ListEvents){
            System.out.println("Summary: " + e.summary + " Start value " + e.start.dateTime.value + " End value " + e.end.dateTime.value);
        }
    }

    // 新增：獲取所有事件的方法
    public List<Event> getAllEvents() {
        try {
            // 每次調用都重新創建 FileReader，確保讀取最新資料
            FileReader reader = new FileReader(EVENTS_FILE_PATH);
            Type listType = new TypeToken<List<Event>>(){}.getType();
            List<Event> events = gson.fromJson(reader, listType);
            reader.close();

            return events != null ? events : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error reading events: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}