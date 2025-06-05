package UI.CalendarUI.service;

import GoogleCalendar.util.JsonUtil;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarServiceImp implements GoogleCalendarService {
    private static final String APPLICATION_NAME = "Google Calendar Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";
    private static final String EVENTS_FILE_PATH = "src/main/resources/events.json";

    private final Calendar calendarService;

    // 建構子：初始化 Google Calendar API 的服務
    public GoogleCalendarServiceImp() throws Exception {
        this.calendarService = initializeCalendarService();
    }

    // 初始化 Google Calendar API 的服務
    private Calendar initializeCalendarService() throws Exception {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                getCredentials()) // 取得 OAuth2 憑證
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // 取得 OAuth2 憑證
    private Credential getCredentials() throws Exception {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH); // 讀取憑證檔案
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH))) // 儲存 Token 的位置
                .setAccessType("offline") // 設定離線存取
                .build();
        return new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(
                flow, new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver()).authorize("user");
    }

    // 從 Google Calendar API 取得事件列表
    @Override
    public List<Event> fetchEvents() throws Exception {
        DateTime now = new DateTime(System.currentTimeMillis()); // 取得目前時間
        Events events = calendarService.events().list("primary") // 從主要行事曆取得事件
                .setTimeMin(now) // 設定最小時間（現在時間）
                .setOrderBy("startTime") // 按開始時間排序
                .setSingleEvents(true) // 只取得單一事件（不包含重複事件）
                .execute();

        return events.getItems(); // 回傳事件列表
    }

    // 將事件列表上傳到 Google Calendar
    @Override
    public void uploadEvents(List<Event> events) throws Exception {
        for (Event event : events) {
            calendarService.events().insert("primary", event).execute(); // 插入每個事件到主要行事曆
        }
    }

    // 刪除指定 ID 的事件
    @Override
    public void deleteEvent(String eventId) throws Exception {
        calendarService.events().delete("primary", eventId).execute(); // 刪除事件
    }

    // 更新指定的事件
    @Override
    public void updateEvent(Event event) throws Exception {
        calendarService.events().update("primary", event.getId(), event).execute(); // 更新事件
    }

    // 插入單一事件到 Google Calendar
    @Override
    public void insertEvent(Event event) throws Exception {
        calendarService.events().insert("primary", event).execute(); // 插入事件
    }

    // 建立新的事件
    @Override
    public Event createEvent(String summary, String location, String description, String startTime, String endTime) {

        // RFC3339 格式的時間格式化器
        DateTimeFormatter RFC3339_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        // 解析開始與結束時間
        ZonedDateTime zoneStartTime = ZonedDateTime.parse(startTime, RFC3339_FORMATTER);
        ZonedDateTime zoneEndTime = ZonedDateTime.parse(endTime, RFC3339_FORMATTER);

        // 建立事件物件
        Event event = new Event()
                .setSummary(summary) // 設定標題
                .setLocation(location) // 設定地點
                .setDescription(description); // 設定描述

        // 設定開始時間
        DateTime startDateTime = new DateTime(RFC3339_FORMATTER.format(zoneStartTime));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Taipei"); // 設定時區
        event.setStart(start);

        // 設定結束時間
        DateTime endDateTime = new DateTime(RFC3339_FORMATTER.format(zoneEndTime));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Taipei"); // 設定時區
        event.setEnd(end);

        return event; // 回傳事件物件
    }

    // 根據指定日期取得事件（目前未實作）
    @Override
    public List<Event> getEventsForDate(LocalDate date) {
        return List.of(); // 回傳空列表
    }

    // 取得事件並儲存到 JSON 檔案
    public void fetchAndSaveEvents() throws Exception {

        // 確保 events.json 檔案存在
        File eventsFile = new File(EVENTS_FILE_PATH);
        if (!eventsFile.exists()) {
            eventsFile.getParentFile().mkdirs(); // 確保父目錄存在
            eventsFile.createNewFile(); // 建立檔案
        }

        // 從 Google Calendar API 取得事件
        List<Event> events = fetchEvents();

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
            // 儲存事件到 JSON 檔案
            JsonUtil.saveEventsToJson(events, EVENTS_FILE_PATH);
            System.out.println("行程已儲存到：" + EVENTS_FILE_PATH);
        }
    }
}