package ChatGPT;

import ChatGPT.ChatGPT;
import ChatGPT.Prompt.EventPrompt;
import UI.service.EventService;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class EventArranger {
    private ChatGPT chatGPT;
    private EventService eventService;

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
            System.out.println("AI Response: " + response);//TODO
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
