package ChatGPT.Prompt;

// Language
import Language.LanguageConfig;

// Json
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Java
import java.io.FileReader;
import java.io.IOException;

public class EventPrompt {
    private String Start;
    private String Learning;
    private String Review;
    private String End;

    private String begin = "現在";
    private String finish = "15分鐘後";
    private String times = "數";
    private String time = "15分鐘";


    public EventPrompt(){
        String language = LanguageConfig.loadLanguage();
        String languageFile;

        // 根據語言選擇不同檔案
        if ("zh".equals(language)) {
            languageFile = "language/Chinese/EventPrompt.json";
        } else if ("en".equals(language)) {
            languageFile = "language/English/EventPrompt.json";
        } else {
            languageFile = "language/English/EventPrompt.json";
        }
        try (FileReader reader = new FileReader(languageFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            Start = jsonObject.get("Start").getAsString();
            Learning = jsonObject.get("Learning").getAsString();
            Review = jsonObject.get("Review").getAsString();
            End = jsonObject.get("End").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //請先設定時間
    public void setTime(String begin, String finish, String times, String time){
        this.begin = begin;
        this.finish = finish;
        this.times = times;
        this.time = time;
    }

    public String getTime(){
        String returnTime = "" + this.begin + "" + this.finish + "" + this.times + "" + this.time + "";
        return returnTime;
    }

    //讓AI安排複習時間
    public String ReviewPrompt (){
        String prompt = Start + Review + End;
        return prompt+"\n";
    }

    //讓AI安排學習計畫
    public String LearningPrompt (){
        String prompt = Start + Learning + End;
        return prompt+"\n";
    }
}
