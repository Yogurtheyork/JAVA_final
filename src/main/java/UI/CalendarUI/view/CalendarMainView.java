package UI.CalendarUI.view;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.view.MonthView;
import UI.CalendarUI.view.WeekView;
import UI.CalendarUI.view.YearView;

import javax.swing.*;
import java.awt.*;

public class CalendarMainView extends JPanel {

    private final CalendarController controller;
    private final JTabbedPane tabbedPane;

    public CalendarMainView(CalendarController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Month", new MonthView(controller));
        tabbedPane.addTab("Week", new WeekView(controller));
        tabbedPane.addTab("Year", new YearView(controller));

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    public void showInFrame(JFrame frame) {
        frame.setTitle("Calendar Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(this);
        frame.setVisible(true);
    }
}
