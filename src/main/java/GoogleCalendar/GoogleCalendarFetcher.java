package GoogleCalendar;

import GoogleCalendar.service.GoogleCalendarService;
import GoogleCalendar.service.GoogleCalendarServiceImpl;
import GoogleCalendar.util.JsonUtil;
import com.google.api.services.calendar.model.Event;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GoogleCalendarFetcher {
    private final GoogleCalendarService calendarService;
    private static final String EVENTS_FILE_PATH = "src/main/resources/events.json";

    //Constructor
    public GoogleCalendarFetcher() throws Exception {
        this.calendarService = new GoogleCalendarServiceImpl();
    }

    public void fetchAndSaveEvents() throws Exception {

        // Ensure the events.json file exists
        File eventsFile = new File(EVENTS_FILE_PATH);
        if (!eventsFile.exists()) {
            eventsFile.getParentFile().mkdirs(); // Ensure parent directories exist
            eventsFile.createNewFile();
        }
        // Fetch events from events.json
        List<Event> events = calendarService.fetchEvents();
        if (events.isEmpty()) {
            System.out.println("找不到行程");
        } else {
            System.out.println("接下來的行程：");
            for (Event event : events) {
                System.out.printf(" - %s (%s ~ %s)\n",
                        event.getSummary(),
                        event.getStart().getDateTime() != null ? event.getStart().getDateTime() : event.getStart().getDate(),
                        event.getEnd().getDateTime() != null ? event.getEnd().getDateTime() : event.getEnd().getDate());
            }
            JsonUtil.saveEventsToJson(events, EVENTS_FILE_PATH);
            System.out.println("行程已儲存到：" + EVENTS_FILE_PATH);
        }
    }

    public void uploadEventsFromFile() throws Exception {
        try {
            List<Event> events = JsonUtil.loadEventsFromJson(EVENTS_FILE_PATH);
            calendarService.uploadEvents(events);
            System.out.println("已成功上傳所有行程");
        } catch (IOException e) {
            System.err.println("找不到 events.json 檔案！");
        }
    }

}
