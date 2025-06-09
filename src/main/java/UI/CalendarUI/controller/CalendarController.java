package UI.CalendarUI.controller;

import UI.AIArrangeUI;
import UI.CalendarUI.model.CalendarModel;
import UI.CalendarUI.service.EventInfo;
import UI.CalendarUI.service.EventService;
import UI.CalendarUI.view.MonthView;
import UI.CalendarUI.view.WeekView;
import UI.CalendarUI.view.dialogs.EventDialog;
import UI.CalendarUI.view.dialogs.NewEventDialog;
import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class CalendarController {

    private final CalendarModel model;
    private final EventService service;
    private Component parentComponent;
    private MonthView monthView;
    private WeekView weekView;
    private Consumer<String> viewSwitcher;

    public CalendarController(CalendarModel model, EventService service) {
        this.model = model;
        this.service = service;
    }

    public void setParentComponent(Component parent) {
        this.parentComponent = parent;
    }

    // 新增：設定 MonthView 引用
    public void setMonthView(MonthView monthView) {
        this.monthView = monthView;
    }

    // 新增：設定 WeekView 引用
    public void setWeekView(WeekView weekView) {
        this.weekView = weekView;
    }

    // 新增：設定視圖切換器
    public void setViewSwitcher(Consumer<String> viewSwitcher) {
        this.viewSwitcher = viewSwitcher;
    }

    public void handleMonthSelected(LocalDate date) {
        if (monthView != null) {
            monthView.update(date);
        }
        if (viewSwitcher != null) {
            viewSwitcher.accept("MONTH");
        }
    }

    public void handleDaySelected(LocalDate date) {
        // 可擴充週視圖邏輯
    }

    // 新增：處理年份選擇（回到年視圖）
    public void handleYearSelected(LocalDate date) {
        if (viewSwitcher != null) {
            viewSwitcher.accept("YEAR");
        }
    }

    // 新增：處理週選擇（進入週視圖）
    public void handleWeekSelected(LocalDate date) {
        if (weekView != null) {
            weekView.update(date);
        }
        if (viewSwitcher != null) {
            viewSwitcher.accept("WEEK");
        }
    }

    // 新增：處理選擇日期並彈出新事件對話框
    public void handleSelectedWithNewEvent(LocalDate date) {
        // 先切換到週視圖
        if (weekView != null) {
            weekView.update(date);
        }
        if (viewSwitcher != null) {
            viewSwitcher.accept("WEEK");
        }

        // 使用 SwingUtilities.invokeLater 確保視圖切換完成後再彈出對話框
        SwingUtilities.invokeLater(() -> {
            showNewEventDialog(date);
        });
    }

    public void registerMonthButton(int month, JButton button, Runnable onClick) {
        button.addActionListener(e -> onClick.run());
    }

    public void showNewEventDialog(LocalDate date) {
        JCheckBox aiCheckBox = new JCheckBox("Arrange by AI");

        NewEventDialog dialog = new NewEventDialog(
                SwingUtilities.getWindowAncestor(parentComponent),
                date,
                (summary, location, description, d, startTime, endTime) -> {
                    try {
                        boolean arrangeByAI = aiCheckBox.isSelected();
                        ZoneId zoneId = ZoneId.systemDefault();
                        ZonedDateTime startDateTime = ZonedDateTime.of(d, java.time.LocalTime.parse(startTime), zoneId);
                        ZonedDateTime endDateTime = ZonedDateTime.of(d, java.time.LocalTime.parse(endTime), zoneId);

                        String startRfc3339 = startDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        String endRfc3339 = endDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                        Event newEvent = service.createEvent(summary, location, description, startRfc3339, endRfc3339);
                        service.insertEvent(newEvent);
                        if (arrangeByAI) {
                            AIArrangeUI aiArrangeUI = new AIArrangeUI(summary);
                            aiArrangeUI.setVisible(true);//TODO AI行程安排視窗fetch and save events
                        }
                        service.fetchAndSaveEvents();

                        List<Event> updatedEvents = service.getEventsOnDate(d);
                        model.setEventsForDate(d, updatedEvents);
                        monthView.update(date);
                        weekView.update(date);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(parentComponent, "Error saving event: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );

        dialog.add(aiCheckBox, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void showEventDialog(EventInfo event) {
        EventDialog dialog = new EventDialog(
                SwingUtilities.getWindowAncestor(parentComponent),
                event,
                service,
                this);
        dialog.setVisible(true);
    }
}