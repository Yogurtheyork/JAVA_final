package UI.CalendarUI;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.model.CalendarModel;
import UI.CalendarUI.service.EventService;
import UI.CalendarUI.service.GoogleCalendarService;
import UI.CalendarUI.service.GoogleCalendarServiceImp;
import UI.CalendarUI.view.CalendarMainView;

import javax.swing.*;
import java.awt.*;

public class CalendarUI extends JPanel {

    public CalendarUI() throws Exception {
        setLayout(new BorderLayout());

        // 初始化 MVC 元件
        GoogleCalendarService googleCalendarService = new GoogleCalendarServiceImp();
        CalendarModel model = new CalendarModel();
        EventService eventService = new EventService(googleCalendarService);
        CalendarController controller = new CalendarController(model, eventService);

        // 嵌入主視圖
        CalendarMainView mainView = new CalendarMainView(controller);
        this.add(mainView, BorderLayout.CENTER);
    }
}
