package UI.service;

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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class googleCalendarServiceImp implements GoogleCalendarService {
    private static final String APPLICATION_NAME = "Google Calendar Service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";

    private final Calendar calendarService;

    public googleCalendarServiceImp() throws Exception {
        this.calendarService = initializeCalendarService();
    }

    private Calendar initializeCalendarService() throws Exception {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials() throws Exception {
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

    @Override
    public List<Event> fetchEvents() throws Exception {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendarService.events().list("primary")
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }

    @Override
    public void uploadEvents(List<Event> events) throws Exception {
        for (Event event : events) {
            calendarService.events().insert("primary", event).execute();
        }
    }

    @Override
    public void deleteEvent(String eventId) throws Exception {
        calendarService.events().delete("primary", eventId).execute();
    }

    @Override
    public void updateEvent(Event event) throws Exception {
        calendarService.events().update("primary", event.getId(), event).execute();
    }

    @Override
    public void insertEvent(Event event) throws Exception{
        calendarService.events().insert("primary", event).execute();
    }

    @Override
    public Event createEvent(String summary, String location, String description, String startTime, String endTime) {

        // Formatter
        DateTimeFormatter RFC3339_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        ZonedDateTime zoneStartTime = ZonedDateTime.parse(startTime, RFC3339_FORMATTER);
        ZonedDateTime zoneEndTime = ZonedDateTime.parse(endTime, RFC3339_FORMATTER);


        // Create the event
        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description);

        // Set start time
        DateTime startDateTime = new DateTime(RFC3339_FORMATTER.format(zoneStartTime));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Taipei");
        event.setStart(start);

        // Set end time
        DateTime endDateTime = new DateTime(RFC3339_FORMATTER.format(zoneEndTime));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Taipei");
        event.setEnd(end);

        return event;
    }
}