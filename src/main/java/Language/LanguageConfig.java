package Language;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageConfig {
    private static final String DEFAULT_LANGUAGE = "en";
    // 讀取設定
    public static String loadLanguage() {
        InputStream is = LanguageConfig.class.getClassLoader().getResourceAsStream("userSetting.json");
        if (is == null) {
            System.err.println("cannot find userSetting.json");
            return DEFAULT_LANGUAGE;
        }

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("language")) {
                return json.get("language").getAsString();
            } else {
                System.err.println("userSetting.json does not contain 'language' key");
                return DEFAULT_LANGUAGE;
            }
        } catch (Exception e) {
            System.err.println("Could not read json:" + e.getMessage());
            return DEFAULT_LANGUAGE;
        }
    }
}
