package ChatGPT;
// ChatGPT
import ChatGPT.Prompt.EventPrompt;
import io.github.cdimascio.dotenv.Dotenv;
// Json
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
// Google Calendar
import com.google.api.services.calendar.model.*;
//Java
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import DataStructures.SimplifiedEvent;
import UI.CalendarUI.service.*;

public class EventArranger {
    private ChatGPT chatGPT;
    private EventPrompt eventPrompt;
    private String prompt;
    private final String EventPATH = "src/main/resources/events.json";
    private String eventTitle = null;
    private GoogleCalendarServiceImp googleService;

    public EventArranger(int selection, String begin, String finish, String times, String duration) throws FileNotFoundException {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY not found in .env file");
            System.exit(1);
        }
        chatGPT = new ChatGPT(apiKey);

        this.eventPrompt = new EventPrompt(eventTitle);
        eventPrompt.setTime(begin, finish, times, duration);
        switch (selection){
            case 0: // 安排學習計畫
                this.prompt = eventPrompt.LearningPrompt();
                break;
            case 1: // 安排複習考試
                this.prompt = eventPrompt.ReviewPrompt();
                break;
            case 2: // 安排專案進度
                this.prompt = eventPrompt.ProjectPrompt();
                break;
            default:
                System.out.println("無效的選擇");
        }
    }

    public void arrangeEvents() {
        try {
            //取得事件安排
            String response = chatGPT.chat(prompt);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println(response);
            // 寫入事件
            googleService = new GoogleCalendarServiceImp();
            List<SimplifiedEvent> events = gson.fromJson(response, new TypeToken<List<SimplifiedEvent>>(){}.getType());
            for (SimplifiedEvent event : events) {
                // 將事件寫入 Google Calendar
                googleService.insertEvent(googleService.createEvent(event.title, event.location, event.description, event.start, event.end));
            }
            googleService.fetchAndSaveEvents();
            System.out.println("事件安排完成");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
