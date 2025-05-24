package ChatGPT;

import ChatGPT.ChatGPT;
import ChatGPT.Prompt.EventPrompt;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class EventArranger {
    private ChatGPT chatGPT;

    public EventArranger() {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY not found in .env file");
            System.exit(1);
        }
        ChatGPT chatGPT = new ChatGPT(apiKey);
    }

    public void arrangeEvents(String prompt) {
        try {
            String response = chatGPT.chat(prompt);
            System.out.println("AI Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
