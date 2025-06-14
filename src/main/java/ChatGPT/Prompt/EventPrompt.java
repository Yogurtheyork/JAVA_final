package ChatGPT.Prompt;

// Json
import com.google.gson.*;
import DataStructures.SimplifiedEvent;
// Java
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.*;
import java.util.*;
// Service
import UI.CalendarUI.service.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EventPrompt {
    private String Start;
    private String Learning;
    private String Review;
    private String Curriculum;
    private String Project;
    private String End;
    private SimplifiedEvent TargetEvent;

    private String begin = "now";
    private String finish = "15 minutes later";
    private String times = "any";
    private String duration = "15 minutes";

    public EventPrompt(SimplifiedEvent TargetEvent) {

        String languageFile = "src/main/resources/language/English/EventPrompt.json";
        loadLanguageSettings(languageFile);
        setTargetEvent(TargetEvent);
    }

    private void loadLanguageSettings(String languageFile) {
        try {
            try (FileReader reader = new FileReader(languageFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                // 安全地獲取 JSON 值
                Start = getJsonStringSafely(jsonObject, "Start", Start);
                Learning = getJsonStringSafely(jsonObject, "Learning", Learning);
                Review = getJsonStringSafely(jsonObject, "Review", Review);
                Curriculum = getJsonStringSafely(jsonObject, "Curriculum", Curriculum);
                Project = getJsonStringSafely(jsonObject, "Project", Project);
                End = getJsonStringSafely(jsonObject, "End", End);

                System.out.println("語言設定檔案載入成功");
            }

        } catch (JsonSyntaxException e) {
            System.err.println("語言設定檔案 JSON 格式錯誤: " + e.getMessage());
            System.err.println("使用預設值");
        } catch (IOException e) {
            System.err.println("無法讀取語言設定檔案: " + languageFile + "，使用預設值");
            System.err.println("錯誤詳情: " + e.getMessage());
        }
    }

    private String getJsonStringSafely(JsonObject jsonObject, String key, String defaultValue) {
        try {
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsString();
            }
        } catch (Exception e) {
            System.err.println("讀取 JSON 鍵值 '" + key + "' 時發生錯誤: " + e.getMessage());
        }
        return defaultValue;
    }

    public void setTargetEvent(SimplifiedEvent TargetEvent) {
        if (TargetEvent == null) {
            System.err.println("TargetEvent is null, using default event");
            this.TargetEvent = new SimplifiedEvent("Default Event", "2023-10-01T10:00:00", "2023-10-01T11:00:00", "Default Location", "No description provided");
        } else {
            this.TargetEvent = TargetEvent;
        }
    }

    public String getTargetEvent(){
        String eventDetails = "Event Details:\n" +
                "Title: " + this.TargetEvent.title + "\n" +
                "Start: " + this.TargetEvent.start + "\n" +
                "End: " + this.TargetEvent.end + "\n" +
                "Location: " + this.TargetEvent.location + "\n" +
                "Description: " + this.TargetEvent.description + "\n";
        return eventDetails;
    }

    public void setTime(String begin, String finish, String times, String duration){
        this.begin = begin != null ? begin : "now";
        this.finish = finish != null ? finish : "15 minutes later";
        this.times = times != null ? times : "any";
        this.duration = duration != null ? duration : "15 minutes";
    }

    public String getTime(){
        return "Please arrange [" + this.times + "] times events from [" + this.begin + "] to [" + this.finish + "] everytimes for [" + this.duration + "].";
    }

    public String ReviewPrompt() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("載入課程表");
            fileChooser.setFileFilter(new FileNameExtensionFilter("課程表文件 (*.csv)", "csv"));
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String CURRICULUMPATH = fileChooser.getSelectedFile().getAbsolutePath();
                String prompt = this.Start + this.jsonToString() + this.Curriculum + CSVToString(CURRICULUMPATH) + this.Review + this.getTime() + this.End;
                System.out.println(prompt);
                return prompt;
            } else {
                throw new Exception("未選擇課程表文件");
            }
        } catch (Exception e) {
            System.err.println("產生複習 Prompt 時發生錯誤: " + e.getMessage());
            return "產生複習 Prompt 失敗";
        }
    }

    public String LearningPrompt() {
        try {
            String prompt = this.Start + this.jsonToString() + Learning + this.getTargetEvent() + this.getTime() + End;
            System.out.println(prompt);
            return prompt;
        } catch (Exception e) {
            System.err.println("產生學習 Prompt 時發生錯誤: " + e.getMessage());
            return "產生學習 Prompt 失敗";
        }
    }

    public String ProjectPrompt() {
        try {
            String prompt = this.Start + this.jsonToString() + Project + this.getTargetEvent() + this.getTime() + End;
            System.out.println(prompt);
            return prompt;
        } catch (Exception e) {
            System.err.println("error generating project prompt: " + e.getMessage());
            return "Fail to generate project prompt";
        }
    }

    // 取出事件
    public String jsonToString() {
        String EVENTPATH = "src/main/resources/events.json";
        try {
            if (!Files.exists(Paths.get(EVENTPATH))) {
                System.err.println("event file does not exist: " + EVENTPATH);
                return "[]";
            }

            String content = Files.readString(Paths.get(EVENTPATH));
            if (content.trim().isEmpty()) {
                System.err.println("event file is empty: " + EVENTPATH);
                return "[]";
            }

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(content);
            if (!jsonElement.isJsonArray()) {
                System.err.println("event file is not a valid JSON array: " + EVENTPATH);
                return "[]";
            }
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<SimplifiedEvent> simplifiedEvents = new ArrayList<>();

            for (JsonElement elem : jsonArray) {
                try {
                    if (!elem.isJsonObject()) {
                        continue;
                    }

                    JsonObject obj = elem.getAsJsonObject();

                    // 根據你的實際 JSON 結構來取值
                    String title = getJsonPropertySafely(obj, "summary");
                    String location = getJsonPropertySafely(obj, "location");
                    String description = getJsonPropertySafely(obj, "description");

                    // 解析開始時間
                    String startStr = extractDateTimeFromGoogleFormat(obj, "start");
                    String endStr = extractDateTimeFromGoogleFormat(obj, "end");

                    simplifiedEvents.add(new SimplifiedEvent(title, startStr, endStr, location, description));

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            Gson outputGson = new GsonBuilder().setPrettyPrinting().create();
            return outputGson.toJson(simplifiedEvents);

        } catch (JsonSyntaxException e) {
            System.err.println(e.getMessage());
            return "[]";
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return "[]";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return "[]";
        }
    }

    private String getJsonPropertySafely(JsonObject obj, String property) {
        try {
            if (obj.has(property) && !obj.get(property).isJsonNull()) {
                return obj.get(property).getAsString();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    // 根據你的 Google Calendar JSON 格式來解析時間
    private String extractDateTimeFromGoogleFormat(JsonObject obj, String timeField) {
        try {
            if (!obj.has(timeField)) {
                return "未設定時間";
            }

            JsonObject timeObj = obj.getAsJsonObject(timeField);
            if (!timeObj.has("dateTime")) {
                return "未設定時間";
            }

            JsonObject dateTimeObj = timeObj.getAsJsonObject("dateTime");
            if (!dateTimeObj.has("value")) {
                return "未設定時間";
            }

            long timeMillis = dateTimeObj.get("value").getAsLong();

            // 取得時區
            String zone = "Asia/Taipei"; // 預設時區
            if (timeObj.has("timeZone")) {
                zone = timeObj.get("timeZone").getAsString();
            }

            return Instant.ofEpochMilli(timeMillis)
                    .atZone(ZoneId.of(zone))
                    .toLocalDateTime()
                    .toString();

        } catch (Exception e) {
            System.err.println("解析時間欄位 '" + timeField + "' 時發生錯誤: " + e.getMessage());
            return "時間解析錯誤";
        }
    }

    public String CSVToString(String PATH) {
        try {
            if (!Files.exists(Paths.get(PATH))) {
                System.err.println("CSV 檔案不存在: " + PATH);
                return "";
            }
            return Files.readString(Paths.get(PATH));
        } catch (IOException e) {
            System.err.println("讀取 CSV 檔案失敗: " + e.getMessage());
            return "";
        }
    }
}

