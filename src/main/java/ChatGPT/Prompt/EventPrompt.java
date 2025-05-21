package ChatGPT.Prompt;

// Language
import Language.LanguageConfig;

// Json
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Java
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class EventPrompt {
    private String Start;
    private String Learning;
    private String Review;
    private String End;

    private String begin = "now";
    private String finish = "15 minutes later";
    private String times = "any";
    private String duration = "15 minutes";


    public EventPrompt(){
        String languageFile = "language/English/EventPrompt.json";
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
    public void setTime(String begin, String finish, String times, String duration){
        this.begin = begin;
        this.finish = finish;
        this.times = times;
        this.duration = duration;
    }

    public String getTime(){
        String returnTime = "Please arrange [" + this.times + "] times events from [" + this.begin + "] to [" + this.finish + "] everytimes for [" + this.duration + "].";
        return returnTime;
    }

    //讓AI安排複習時間
    public String ReviewPrompt (String PATH){
        String prompt = this.Start + this.jsonToString(PATH) + this.Review + this.getTime() + this.End;
        return prompt;
    }

    //讓AI安排學習計畫
    public String LearningPrompt (String PATH){
        String prompt = Start + this.jsonToString(PATH) + Learning + this.getTime() + End;
        return prompt;
    }

    public String jsonToString(String PATH){
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(PATH)));
            return jsonString;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
