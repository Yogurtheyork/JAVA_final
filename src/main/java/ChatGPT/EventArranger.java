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
    private EventService eventService;
    private EventPrompt eventPrompt = new EventPrompt();
    private final String EventPATH = "src/main/resources/event.json";

    public EventArranger() {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY not found in .env file");
            System.exit(1);
        }
        chatGPT = new ChatGPT(apiKey);
        eventService = new EventService();
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
