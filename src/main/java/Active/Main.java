package Active;

import UI.*;
import UI.service.EventService;
import UI.controller.CalendarController;
import UI.view.CalendarView;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Calendar");
        frame.setSize(1000,1000);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize the calendar components
        EventService eventService = new EventService();
        CalendarController controller = new CalendarController(eventService, null);
        CalendarView view = new CalendarView(eventService, controller);
        controller.setView(view);
        
        // Create and show the CalendarUI
        CalendarUI calendarUI = new CalendarUI();
        calendarUI.setVisible(true);

        frame.add(calendarUI);
        //Curriculum Curriculum = new Curriculum();
        //Curriculum.setVisible(true);
//        ChatRoom chatWindow = new ChatRoom();
//        chatWindow.setVisible(true);
    }
}