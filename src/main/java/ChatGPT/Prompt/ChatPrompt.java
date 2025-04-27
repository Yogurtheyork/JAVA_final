package ChatGPT.Prompt;

import ChatGPT.ChatConfig;

public class ChatPrompt {
    // TODO: 將語言轉換寫進json檔
    private static final String ChatRoomChinese =
            "你是一個[智慧行事曆管理與學習助理]，請協助我[安排行程]及[學習]，[不提供其他功能]。" +
                    "接下來的無論是甚麼問題都請以[繁體中文]為主，專有名詞[英文或原文]為輔回答。"+
                    "收到請回答\"您好我是您的智慧助理，我可以如何幫助您?\"";
    private static final String ChatRoomEnglish =
            "You are a [Smart Calendar Management and Learning Assistant], please assist me with [Scheduling] and [Learning], [No other functions].What " +
                    "Whatever the question is, please answer in [English] as the main language, and [original text] as a supplement." +
                    "If you receive this, please reply \"Hello, I am your smart assistant, how can I help you?\"";


    public String strPrompt (){
        String prompt;
        if (ChatConfig.loadLanguage().equals("zh")) {
            prompt = ChatRoomChinese;
        } else if(ChatConfig.loadLanguage().equals("en")) {
            prompt = ChatRoomEnglish;
        } else {
            prompt = ChatRoomEnglish;
        }
        return prompt+"\n";
    }
}
