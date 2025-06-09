package UI.CalendarUI.service;

import UI.CalendarUI.service.EventInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class JsonService {

    private static final String EVENTS_FILE_PATH = "src/main/resources/events.json"; // 事件 JSON 檔案的路徑
    Gson gson; // 用於處理 JSON 的 Gson 物件
    FileReader reader; // 用於讀取檔案的 FileReader 物件

    // 建構子：初始化 Gson 並嘗試讀取事件檔案
    public JsonService(){
        gson = new Gson(); // 初始化 Gson
        reader = null; // 初始化 reader 為 null
        try {
            reader = new FileReader(EVENTS_FILE_PATH); // 嘗試讀取事件檔案
        }catch (FileNotFoundException e){
            System.out.println("File events.json was not found."); // 如果檔案不存在，輸出錯誤訊息
        }
    }

    // 方法：回傳事件資訊
    public void getEventInfo(){
        Type listType = new TypeToken<List<EventInfo>>(){}.getType();
        List<EventInfo> ListEvents = gson.fromJson(reader, listType);

        for (EventInfo e  : ListEvents){
            System.out.println("Summary: " + e.summary + " Start value " + e.start.dateTime.value + " End value " + e.end.dateTime.value);
        }
    }

    // 新增：獲取所有事件的方法
    public List<EventInfo> getAllEvents() {
        try {
            // 每次調用都重新創建 FileReader，確保讀取最新資料
            FileReader reader = new FileReader(EVENTS_FILE_PATH);
            Type listType = new TypeToken<List<EventInfo>>(){}.getType();
            List<EventInfo> events = gson.fromJson(reader, listType);
            reader.close();

            return events != null ? events : new ArrayList<>(); // 如果事件列表為 null，回傳空列表
        } catch (Exception e) {
            System.out.println("Error reading events: " + e.getMessage()); // 如果發生錯誤，輸出錯誤訊息
            return new ArrayList<>(); // 回傳空列表
        }
    }
}