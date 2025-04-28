package Language;

import DataStructures.Setting;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

public class LanguageConfig {
    private static final String SETTINGS_FILE = "src/main/resources/userSetting.json";
    private static final String DEFAUT_lANGUAGE = "English";
    // 讀取設定
    public static String loadLanguage() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(SETTINGS_FILE)) {
            Setting setting = gson.fromJson(reader, Setting.class);
            if (setting == null) {
                System.err.println("Setting is null, using default setting");
                return DEFAUT_lANGUAGE;
            }
            return setting.language;
        } catch (IOException e) {
            System.err.println("Could not read settings file: " + e.getMessage());
            return DEFAUT_lANGUAGE;
        }
    }
}   
