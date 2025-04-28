package ChatGPT.Prompt;

import ChatGPT.ChatConfig;

public class EventPrompt {
    // TODO: 將語言轉換寫進json檔
    private static final String ChineseStart =
            "請作為一個[學習助理]協助我安排行程，並提供我[行程名稱]、[開始時間]、[結束時間]、[地點]、[備註]等資訊。" +
                    "以下json格式資料是[現有的行程]，";
    private static final String ChineseLearning = "";
    private static final String ChineseReview = "";
    private static final String ChineseEnd = "";
    private static final String EnglishStart =
            "Please act as a [Learning Assistant] to help me schedule, and provide me with information such as [Event Name], [Start Time], [End Time], [Location], and [Notes]." +
                    "The following json format data is [existing schedule],";
    private static final String EnglishLearning = "";
    private static final String EnglishReview = "";
    private static final String EnglishEnd = "";

    public String ReviewPrompt (){
        String prompt;
        if (ChatConfig.loadLanguage().equals("zh")) {
            prompt = ChineseStart+
                    ChineseReview+
                    ChineseEnd;
        } else if(ChatConfig.loadLanguage().equals("en")) {
            prompt = EnglishStart+
                    EnglishReview+
                    EnglishEnd;
        } else {
            prompt = EnglishStart+
                    EnglishReview+
                    EnglishEnd;
        }
        return prompt+"\n";
    }
}
