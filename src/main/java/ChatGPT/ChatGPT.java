package ChatGPT;

import com.google.gson.Gson; // Import Gson for JSON parsing
import okhttp3.*; // Import OkHttp for HTTP requests
import java.io.IOException; // Import IOException for error handling
import java.util.ArrayList; // Import ArrayList for dynamic arrays
import java.util.List; // Import List for generic collections

public class ChatGPT {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;
    private final List<Message> conversationHistory;

    public ChatGPT(String apiKey){
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.conversationHistory = new ArrayList<>();
    }

    public String chat(String message) throws IOException {
        // Add user message to conversation history
        conversationHistory.add(new Message("user", message));

        // Create request body
        ChatRequest requestBody = new ChatRequest(
            "gpt-3.5-turbo",
            conversationHistory
        );

        // Create HTTP request
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
            .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            String responseBody = response.body().string();
            ChatResponse chatResponse = gson.fromJson(responseBody, ChatResponse.class);
            
            // Get the assistant's response
            String assistantResponse = chatResponse.choices.get(0).message.content;
            
            // Add assistant's response to conversation history
            conversationHistory.add(new Message("assistant", assistantResponse));
            
            return assistantResponse;
        }
    }

    // Inner classes for JSON serialization/deserialization
    private static class ChatRequest {
        String model;
        List<Message> messages;

        ChatRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    private static class ChatResponse {
        List<Choice> choices;
    }

    private static class Choice {
        Message message;
    }

    private static class Message {
        String role;
        String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
