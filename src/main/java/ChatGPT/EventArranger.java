package ChatGPT;

import ChatGPT.ChatGPT;
import ChatGPT.Prompt.EventPrompt;
import UI.service.EventService;
import io.github.cdimascio.dotenv.Dotenv;
import com.google.gson.*;
import java.io.FileWriter;
import java.io.IOException;

public class EventArranger {
    private ChatGPT chatGPT;
    private EventPrompt eventPrompt;
    private final String EventPATH = "src/main/resources/event.json";

    public EventArranger(int selection, String begin, String finish, String times, String duration) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY not found in .env file");
            System.exit(1);
        }
        chatGPT = new ChatGPT(apiKey);

        this.eventPrompt = new EventPrompt();
        eventPrompt.setTime(begin, finish, times, duration);
        switch (selection){
            case 0: // 安排學習計畫
                arrangeEvents(eventPrompt.LearningPrompt());
                break;
            case 1: // 安排複習考試
                arrangeEvents(eventPrompt.ReviewPrompt());
                break;
            case 2: // 安排專案進度
                arrangeEvents(eventPrompt.ProjectPrompt());
                break;
            default:
                System.out.println("無效的選擇");
        }
    }

    public void arrangeEvents(String prompt) {
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
}
