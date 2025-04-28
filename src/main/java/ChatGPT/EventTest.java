package ChatGPT;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.Scanner;
import ChatGPT.Prompt.ChatPrompt;

public class EventTest {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY not found in .env file");
            System.exit(1);
        }

        ChatGPT chatGPT = new ChatGPT(apiKey);
        Scanner scanner = new Scanner(System.in);

        ChatPrompt chatPrompt = new ChatPrompt();
        String config = chatPrompt.strPrompt();
        try {
            System.out.println("Bot: " + chatGPT.chat(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                String response = chatGPT.chat(userInput);
                System.out.println("Bot: " + response);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}