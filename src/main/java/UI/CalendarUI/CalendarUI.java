package UI.CalendarUI;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.model.CalendarModel;
import UI.CalendarUI.service.EventService;
import UI.CalendarUI.view.MonthView;
import UI.CalendarUI.view.Switcher.ViewSwitcher;
import UI.CalendarUI.view.WeekView;
import UI.CalendarUI.view.YearView;

import javax.swing.*;
import java.awt.*;

public class CalendarUI extends JPanel {
    public CalendarUI() throws Exception {
        setLayout(new BorderLayout());

        CalendarModel model = new CalendarModel();
        EventService eventService = new EventService();
        CalendarController controller = new CalendarController(model, eventService);
        controller.setParentComponent(this);

        YearView yearView = new YearView(controller);
        MonthView monthView = new MonthView(controller);
        WeekView weekView = new WeekView(controller);

        // 設定 MonthView 和 WeekView 引用到 Controller
        controller.setMonthView(monthView);
        controller.setWeekView(weekView);

        JPanel viewContainer = new JPanel();
        ViewSwitcher switcher = new ViewSwitcher(viewContainer);
        switcher.addView("YEAR", yearView);
        switcher.addView("MONTH", monthView);
        switcher.addView("WEEK", weekView);

        // 設定視圖切換器到 Controller
        controller.setViewSwitcher(switcher::show);

        add(viewContainer, BorderLayout.CENTER);
    }
}