package Active;

import UI.*;

public class Main {
    public static void main(String[] args) {
        //呼叫行事曆UI
        CalendarUI mainWindow = new CalendarUI();
        mainWindow.setVisible(true);
        Curriculum curriculumWindow = new Curriculum();
        curriculumWindow.setVisible(true);
        ChatRoom chatWindow = new ChatRoom();
        chatWindow.setVisible(true);
        //測試
    }
}