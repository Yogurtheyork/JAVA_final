package GoogleCalendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.api.client.util.DateTime;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleCalendarFetcher {

    private static final String APPLICATION_NAME = "Google Calendar Fetcher";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";

    // 登入資訊
    private static com.google.api.client.auth.oauth2.Credential getCredentials() throws Exception {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(
                flow, new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver()).authorize("user");
    }

    // 取得行程
    public void fetchEvents() throws Exception {
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        Date now = new Date();
        Events events = service.events().list("primary")
                .setTimeMin(new DateTime(now))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("找不到行程");
        } else {
            System.out.println("接下來的行程：");

            List<Map<String, String>> jsonList = new ArrayList<>();

            for (Event event : items) {
                String summary = event.getSummary();
                String start = event.getStart().getDateTime() != null ?
                        event.getStart().getDateTime().toString() :
                        event.getStart().getDate().toString();
                String end = event.getEnd().getDateTime() != null ?
                        event.getEnd().getDateTime().toString() :
                        event.getEnd().getDate().toString();
                String location = event.getLocation() != null ? event.getLocation() : "（無地點）";
                String description = event.getDescription() != null ? event.getDescription() : "（無描述）";

                System.out.printf(" - %s (%s ~ %s)\n", summary, start, end);

                Map<String, String> jsonEvent = new LinkedHashMap<>();
                jsonEvent.put("summary", summary);
                jsonEvent.put("start", start);
                jsonEvent.put("end", end);
                jsonEvent.put("location", location);
                jsonEvent.put("description", description);

                jsonList.add(jsonEvent);
            }

            try (Writer writer = new FileWriter("events.json")) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(jsonList, writer);
                System.out.println("行程已儲存到：" + new File("src/main/resources/events.json").getAbsolutePath());
            }
        }
    }

    // 上傳行程
    public void uploadEvents() throws Exception {
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        try (Reader reader = new FileReader("src/main/resources/events.json")) {
            Gson gson = new Gson();
            List<Map<String, String>> jsonList = gson.fromJson(reader, new TypeToken<List<Map<String, String>>>(){}.getType());

            for (Map<String, String> jsonEvent : jsonList) {
                Event event = new Event()
                        .setSummary(jsonEvent.get("summary"))
                        .setLocation(jsonEvent.get("location"))
                        .setDescription(jsonEvent.get("description"));

                DateTime startDateTime = new DateTime(jsonEvent.get("start"));
                EventDateTime start = new EventDateTime().setDateTime(startDateTime);
                event.setStart(start);

                DateTime endDateTime = new DateTime(jsonEvent.get("end"));
                EventDateTime end = new EventDateTime().setDateTime(endDateTime);
                event.setEnd(end);

                service.events().insert("primary", event).execute();
                System.out.println("已上傳行程：" + jsonEvent.get("summary"));
            }
        } catch (FileNotFoundException e) {
            System.err.println("找不到 events.json 檔案！");
        }
    }

//    // 主程式：提供 fetch/upload 選擇
//    public static void main(String[] args) {
//        GoogleCalendarFetcher fetcher = new GoogleCalendarFetcher();
//        try{
//            fetcher.fetchEvents();
//            //fetcher.uploadEvents();
//        } catch (Exception e) {
//            System.err.println("執行時發生錯誤：" + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
