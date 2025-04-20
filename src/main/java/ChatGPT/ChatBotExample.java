package ChatGPT;

import java.io.IOException;
import java.util.Scanner;

public class ChatBotExample {
    public static void main(String[] args) {
        // Replace with your actual OpenAI API key
        String apiKey = "your-api-key-here";
        ChatGPT chatGPT = new ChatGPT(apiKey);
        Scanner scanner = new Scanner(System.in);

        System.out.println("ChatGPT Bot (Type 'exit' to quit)");
        System.out.println("--------------------------------");

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