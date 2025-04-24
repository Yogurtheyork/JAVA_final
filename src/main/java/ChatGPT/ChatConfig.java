package ChatGPT;

import DataStructures.Setting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChatConfig {
    private static final String CONFIG_FILE = "src/main/resources/config.json";
    private static final String SETTINGS_FILE = "src/main/resources/userSetting.json";
    private static final Config DEFAULT_CONFIG = new Config("zh");
    // 設定檔內容
    public static class Config {
        public String languagePreference;
        // TODO: 可以擴展更多設定項目
        public Config(String languagePreference) {
            this.languagePreference = languagePreference;
        }
    }

    // 儲存設定
    public static void saveConfig(Config config) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(config, writer);
            System.out.println("設定已儲存至 config.json");
        } catch (IOException e) {
            System.err.println("無法儲存設定: " + e.getMessage());
        }
    }

    // 讀取設定
    public static String loadLanguage() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(SETTINGS_FILE)) {
            Setting setting = gson.fromJson(reader, Setting.class);
            if (setting == null) {
                System.err.println("設定檔為空，使用預設設定");
                return DEFAULT_CONFIG.languagePreference;
            }
            return setting.language;
        } catch (IOException e) {
            System.err.println("找不到設定檔或讀取錯誤，使用預設設定");
            return DEFAULT_CONFIG.languagePreference;
        }
    }

    public String ChatPrompt (){
        String prompt;
        if (loadLanguage().equals("zh")) {
            // 繁體中文提示
            prompt = "你是一個[智慧行事曆管理與學習助理]，請協助我[安排行程]及[學習]，[不提供其他功能]。\n" +
                    "收到請回答\"您好我是您的智慧助理，我可以如何幫助您?\"";
        } else if(loadLanguage().equals("en")) {
            // 英文提示
            prompt = "You are a [Smart Calendar Management and Learning Assistant], please assist me with [Scheduling] and [Learning], [No other functions].\n" +
                    "If you receive this, please reply \"Hello, I am your smart assistant, how can I help you?\"";
        } else {
            // 英文提示
            prompt = "You are a [Smart Calendar Management and Learning Assistant], please assist me with [Scheduling] and [Learning], [No other functions].\n" +
                    "If you receive this, please reply \"Hello, I am your smart assistant, how can I help you?\"";
        }
        return prompt;
    }

//    // 測試主程式
//    public static void main(String[] args) {
//        // 儲存繁體中文為語言偏好
//        Config config = new Config(loadLanguage());
//        saveConfig(config);
//    }
}
