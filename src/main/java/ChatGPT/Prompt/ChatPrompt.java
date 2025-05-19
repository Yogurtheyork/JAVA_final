package ChatGPT.Prompt;

// Language
import Language.LanguageConfig;

// Json
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Java
import java.io.FileReader;
import java.io.IOException;

public class ChatPrompt {
    private String chatRoomPrompt;  // 存讀到的 "ChatRoom" 文字

    public ChatPrompt() {
        // 建構子：一創好物件就自動讀資料
        loadPrompt();
    }
    private void loadPrompt() {
        String language = LanguageConfig.loadLanguage();
        String languageFile;

        // 根據語言選擇不同檔案
        if (language.equals("zh")) {
            languageFile = "src/main/resources/language/Chinese/ChatPrompt.json";
        } else if (language.equals("en")) {
            languageFile = "src/main/resources/language/English/ChatPrompt.json";
        } else {
            languageFile = "src/main/resources/language/English/ChatPrompt.json";
        }

        try (FileReader reader = new FileReader(languageFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            chatRoomPrompt = jsonObject.get("ChatRoom").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            chatRoomPrompt = "Default ChatRoom Prompt";
        }
    }

    public String strPrompt() {
        return chatRoomPrompt + "\n";
    }
}