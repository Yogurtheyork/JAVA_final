package ChatGPT;
// ChatGPT
import ChatGPT.Prompt.EventPrompt;
import io.github.cdimascio.dotenv.Dotenv;
// Json
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
// Google Calendar
import com.google.api.services.calendar.model.*;
//Java
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.io.StringReader;
import UI.CalendarUI.service.*;
import DataStructures.*;

public class EventArranger {
    private ChatGPT chatGPT;
    private EventPrompt eventPrompt;
    private String prompt;
    private final String EventPATH = "src/main/resources/events.json";
    private GoogleCalendarServiceImp googleService;

    public EventArranger(int selection, String begin, String finish, String times, String duration, SimplifiedEvent TargetEvent) throws FileNotFoundException {
        // 初始化 ChatGPT
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY not found in .env file");
            System.exit(1);
        }
        chatGPT = new ChatGPT(apiKey);

        // 初始化 EventPrompt
        this.eventPrompt = new EventPrompt(TargetEvent);
        eventPrompt.setTime(begin, finish, times, duration);

        // 設置提示詞
        switch (selection) {
            case 0: // 安排學習計畫
                this.prompt = eventPrompt.LearningPrompt();
                break;
            case 1: // 安排複習考試
                this.prompt = eventPrompt.ReviewPrompt();
                break;
            case 2: // 安排專案進度
                this.prompt = eventPrompt.ProjectPrompt();
                break;
            default:
                System.out.println("無效的選擇");
        }
    }

    public void arrangeEvents() {
        try {
            // 取得事件安排
            String response = chatGPT.chat(prompt);
            System.out.println("ChatGPT Response: " + response);

            // 使用 JsonReader 進行寬鬆解析
            JsonReader reader = new JsonReader(new StringReader(cleanJsonString(response)));
            reader.setLenient(true);

            // 設置 JSON 解析器
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            List<SimplifiedEvent> events = null;
            try {
                // 先將回應解析為 JsonElement
                JsonElement jsonElement = JsonParser.parseReader(reader);

                // 嘗試直接解析為事件列表
                try {
                    if (jsonElement.isJsonArray()) {
                        events = gson.fromJson(jsonElement, new TypeToken<List<SimplifiedEvent>>(){}.getType());
                    } else if (jsonElement.isJsonObject()) {
                        // 嘗試從 events 字段獲取事件數組
                        JsonArray eventArray = jsonElement.getAsJsonObject().getAsJsonArray("events");
                        events = gson.fromJson(eventArray, new TypeToken<List<SimplifiedEvent>>(){}.getType());
                    }
                } catch (Exception e) {
                    System.out.println("第一次解析失敗，嘗試清理後重新解析");
                    // 如果解析失敗，嘗試更深層的清理
                    String deepCleanedJson = deepCleanJson(jsonElement.toString());
                    events = gson.fromJson(deepCleanedJson, new TypeToken<List<SimplifiedEvent>>(){}.getType());
                }
            } catch (JsonSyntaxException e) {
                System.out.println("JSON 解析錯誤: " + e.getMessage());
                throw e;
            }

            if (events != null && !events.isEmpty()) {
                googleService = new GoogleCalendarServiceImp();
                for (SimplifiedEvent event : events) {
                    try {
                        String startTime = addTimeZoneIfNeeded(event.start);
                        String endTime = addTimeZoneIfNeeded(event.end);

                        // 在創建事件之前進行額外的格式檢查
                        if (startTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}") &&
                                endTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}")) {

                            googleService.insertEvent(googleService.createEvent(
                                    event.title,
                                    event.location,
                                    event.description,
                                    startTime,
                                    endTime
                            ));
                        } else {
                            System.out.println("跳過無效的時間格式: " + event.title);
                        }
                    } catch (Exception e) {
                        System.out.println("處理事件時出錯: " + event.title + " - " + e.getMessage());
                        continue; // 跳過這個事件，繼續處理下一個
                    }
                }
                googleService.fetchAndSaveEvents();
                System.out.println("Events arranged and saved successfully!");
            } else {
                System.out.println("Error: No valid events found in the response.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String deepCleanJson(String json) {
        // 移除所有換行符和多餘的空白
        json = json.replaceAll("\\s+", " ").trim();

        // 確保字符串值正確包含引號
        json = json.replaceAll("([\\{\\[,]\\s*)([a-zA-Z_][a-zA-Z0-9_]*)(\\s*:)", "$1\"$2\"$3");

        // 修復可能的尾隨逗號
        json = json.replaceAll(",\\s*([\\}\\]])", "$1");

        // 確保數組和對象正確閉合
        if (json.startsWith("{") && !json.endsWith("}")) {
            json += "}";
        } else if (json.startsWith("[") && !json.endsWith("]")) {
            json += "]";
        }

        return json;
    }

    private String cleanJsonString(String json) {
        json = json.trim();

        // 找到第一個有效的 JSON 起始符
        int start = Math.max(json.indexOf('{'), json.indexOf('['));
        if (start == -1) {
            return json;
        }

        // 找到對應的結束符
        int end;
        if (json.charAt(start) == '{') {
            end = findMatchingBrace(json, start, '{', '}');
        } else {
            end = findMatchingBrace(json, start, '[', ']');
        }

        return (end != -1) ? json.substring(start, end + 1) : json;
    }

    private int findMatchingBrace(String json, int start, char open, char close) {
        int count = 1;
        for (int i = start + 1; i < json.length(); i++) {
            if (json.charAt(i) == open) count++;
            else if (json.charAt(i) == close) count--;
            if (count == 0) return i;
        }
        return -1;
    }

    private String addTimeZoneIfNeeded(String dateTime) {
        try {
            // 移除可能存在的毫秒部分
            dateTime = dateTime.replaceAll("\\.\\d+", "");

            // 確保時間格式正確
            if (!dateTime.contains("+") && !dateTime.contains("Z")) {
                return dateTime + "+08:00";  // 添加台北時區（UTC+8）
            }

            // 如果已經包含時區信息，確保格式正確
            if (dateTime.contains(".")) {
                // 如果包含毫秒，移除它
                int dotIndex = dateTime.indexOf('.');
                int tzIndex = Math.max(dateTime.indexOf('+'), dateTime.indexOf('-', dotIndex));
                if (tzIndex > dotIndex) {
                    dateTime = dateTime.substring(0, dotIndex) + dateTime.substring(tzIndex);
                }
            }

            return dateTime;
        } catch (Exception e) {
            System.out.println("時間格式處理警告: " + e.getMessage());
            return dateTime.replaceAll("\\.\\d+", "") + "+08:00";
        }
    }
}