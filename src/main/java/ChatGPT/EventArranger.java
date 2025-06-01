package ChatGPT;

import ChatGPT.ChatGPT;
import ChatGPT.Prompt.EventPrompt;
import UI.CalendarUI.service.EventService;
import io.github.cdimascio.dotenv.Dotenv;
import com.google.gson.*;
import java.io.FileWriter;
import java.io.IOException;

public class EventArranger {
    private ChatGPT chatGPT;
    private EventPrompt eventPrompt;
    private String prompt;
    private final String EventPATH = "src/main/resources/events.json";
    private String eventTitle = null;

    public EventArranger(int selection, String begin, String finish, String times, String duration) {
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
            String response = chatGPT.chat(prompt);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = JsonParser.parseString(response);
            FileWriter writer = new FileWriter(EventPATH);
            gson.toJson(jsonElement, writer);
            writer.close();
            System.out.println("事件安排完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
