package ChatGPT.Prompt;

// Json
import com.google.gson.*;
//Google Calendar
import UI.CalendarUI.service.GoogleCalendarServiceImp;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
// Java
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.*;
import java.util.*;

public class EventPrompt {
    private String Start;
    private String Learning;
    private String Review;
    private String Curriculum;
    private String Project;
    private String End;

    private String eventTitle = "nothing";
    private String begin = "now";
    private String finish = "15 minutes later";
    private String times = "any";
    private String duration = "15 minutes";

    private GoogleCalendarServiceImp googleCalendarService;


    public EventPrompt(String eventTitle) {
        String languageFile = "src/main/resources/language/English/EventPrompt.json";
        try (FileReader reader = new FileReader(languageFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            setEventTitle(eventTitle);
            Start = jsonObject.get("Start").getAsString();
            Learning = jsonObject.get("Learning").getAsString();
            Review = jsonObject.get("Review").getAsString();
            Curriculum = jsonObject.get("Curriculum").getAsString();
            Project = jsonObject.get("Project").getAsString();
            End = jsonObject.get("End").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEventTitle(String eventTitle){
        this.eventTitle = eventTitle;
    }

    public String getEventTitle(){
        return this.eventTitle;
    }

    //請先設定時間
    public void setTime(String begin, String finish, String times, String duration){
        this.begin = begin;
        this.finish = finish;
        this.times = times;
        this.duration = duration;
    }
    //時間組合成prompt字串
    public String getTime(){
        String returnTime = "Please arrange [" + this.times + "] times events from [" + this.begin + "] to [" + this.finish + "] everytimes for [" + this.duration + "].";
        return returnTime;
    }
    //讓AI安排複習時間
    public String ReviewPrompt () throws FileNotFoundException {
        String CURRICULUMPATH = "src/main/resources/curriculum.csv";
        String prompt = this.Start + this.jsonToString() + this.Curriculum + CSVToString(CURRICULUMPATH) + this.Review + this.getTime() + this.End;
        System.out.println(prompt);
        return prompt;
    }
    //讓AI安排學習計畫
    public String LearningPrompt () throws FileNotFoundException {
        String prompt = this.Start + this.jsonToString() + Learning + this.getEventTitle() + this.getTime() + End;
        System.out.println(prompt);
        return prompt;
    }
    //讓AI安排專案規劃
    public String ProjectPrompt () throws FileNotFoundException {
        String prompt = this.Start + this.jsonToString() + Project + this.getEventTitle() + this.getTime() + End;
        System.out.println(prompt);
        return prompt;
    }
    //現有事件轉換成字串
    public String jsonToString() throws FileNotFoundException {
        String  EVENTPATH= "src/main/resources/events.json";
        JsonParser parser = new JsonParser();
        // 讀入 JSON 檔案
        JsonArray jsonArray = parser.parse(new FileReader("EVENTPATH")).getAsJsonArray();

        List<SimplifiedEvent> simplifiedEvents = new ArrayList<>();

        for (JsonElement elem : jsonArray) {
            JsonObject obj = elem.getAsJsonObject();

            String title = obj.has("summary") ? obj.get("summary").getAsString() : "";
            String location = obj.has("location") ? obj.get("location").getAsString() : "";
            String description = obj.has("description") ? obj.get("description").getAsString() : "";

            // 取出 start time
            long startMillis = obj.getAsJsonObject("start")
                    .getAsJsonObject("dateTime")
                    .get("value")
                    .getAsLong();

            long endMillis = obj.getAsJsonObject("end")
                    .getAsJsonObject("dateTime")
                    .get("value")
                    .getAsLong();

            // 轉為 ISO 字串（+08:00）
            ZoneId zone = ZoneId.of("Asia/Taipei");
            String startStr = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDateTime().toString();
            String endStr = Instant.ofEpochMilli(endMillis).atZone(zone).toLocalDateTime().toString();

            simplifiedEvents.add(new SimplifiedEvent(title, startStr, endStr, location, description));
        }
        Gson outputGson = new GsonBuilder().setPrettyPrinting().create();
        String outputJson = outputGson.toJson(simplifiedEvents);

        return outputJson;
    }
    //課表轉換成字串
    public String CSVToString (String PATH){
        try {
            String content = Files.readString(Paths.get(PATH)); // Java 11+
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "nothing";
    }

}

class SimplifiedEvent {
    String title;
    String start;
    String end;
    String location;
    String description;

    public SimplifiedEvent(String title, String start, String end, String location, String description) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.location = location;
        this.description = description;
    }
}