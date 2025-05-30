package ChatGPT.Prompt;

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
    private String Curriculum;
    private String Project;
    private String End;

    private String eventTitle = "nothing";
    private String begin = "now";
    private String finish = "15 minutes later";
    private String times = "any";
    private String duration = "15 minutes";

    private final String EVENTPATH = "src/main/resources/calendar_events.json";

    public EventPrompt(String eventTitle) {
        String languageFile = "src/main/resources/language/English/EventPrompt.json";
        try (FileReader reader = new FileReader(languageFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            setEventTitle(eventTitle);
            Start = jsonObject.get("Start").getAsString();
            Learning = jsonObject.get("Learning").getAsString();
            Review = jsonObject.get("Review").getAsString();
            Curriculum = jsonObject.get("Curriculum").getAsString();
            Project = jsonObject.get("Project").getAsString();
            End = jsonObject.get("End").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEventTitle(String eventTitle){
        this.eventTitle = eventTitle;
    }

    public String getEventTitle(){
        return this.eventTitle;
    }

    //請先設定時間
    public void setTime(String begin, String finish, String times, String duration){
        this.begin = begin;
        this.finish = finish;
        this.times = times;
        this.duration = duration;
    }
    //時間組合成prompt字串
    public String getTime(){
        String returnTime = "Please arrange [" + this.times + "] times events from [" + this.begin + "] to [" + this.finish + "] everytimes for [" + this.duration + "].";
        return returnTime;
    }
    //讓AI安排複習時間
    public String ReviewPrompt (){
        String CURRICULUMPATH = "src/main/resources/curriculum.csv";
        String prompt = this.Start + this.jsonToString() + this.Curriculum + CSVToString(CURRICULUMPATH) + this.Review + this.getTime() + this.End;
        return prompt;
    }
    //讓AI安排學習計畫
    public String LearningPrompt (){
        String prompt = this.Start + this.jsonToString() + Learning + this.getEventTitle() + this.getTime() + End;
        return prompt;
    }
    //讓AI安排專案規劃
    public String ProjectPrompt (){
        String prompt = this.Start + this.jsonToString() + Project + this.getEventTitle() + this.getTime() + End;
        return prompt;
    }
    //現有事件轉換成字串
    public String jsonToString(){
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(EVENTPATH)));
            return jsonString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "nothing";
    }
    //課表轉換成字串
    public String CSVToString (String PATH){
        try {
            String content = Files.readString(Paths.get(PATH)); // Java 11+
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "nothing";
    }

}
