package UI.CalendarUI.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.service.JsonService;
import UI.CalendarUI.service.EventInfo;

public class WeekView extends JPanel {

    private JTable weekTable;
    private DefaultTableModel tableModel;
    private JLabel weekLabel;
    private JButton prevWeekBtn, nextWeekBtn, todayBtn;

    private LocalDate startOfWeek;

    private CalendarController controller;
    private JsonService jsonService; // 新增：用於讀取事件資料

    public WeekView(CalendarController controller) {
        this.controller = controller;
        this.jsonService = new JsonService(); // 初始化 JsonService

        this.setLayout(new BorderLayout());
        initHeader();
        initWeekTable();

        startOfWeek = getStartOfCurrentWeek(LocalDate.now());
        updateWeek();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        weekLabel = new JLabel("", SwingConstants.CENTER);
        weekLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        // 讓週標題可以點擊回到月視圖
        weekLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.handleMonthSelected(startOfWeek);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                weekLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                weekLabel.setForeground(Color.BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                weekLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                weekLabel.setForeground(Color.BLACK);
            }
        });

        headerPanel.add(weekLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        prevWeekBtn = new JButton("<");
        nextWeekBtn = new JButton(">");
        todayBtn = new JButton("Today");

        prevWeekBtn.addActionListener(e -> changeWeek(-1));
        nextWeekBtn.addActionListener(e -> changeWeek(1));
        todayBtn.addActionListener(e -> {
            startOfWeek = getStartOfCurrentWeek(LocalDate.now());
            updateWeek();
        });

        btnPanel.add(prevWeekBtn);
        btnPanel.add(todayBtn);
        btnPanel.add(nextWeekBtn);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);
    }

    private void initWeekTable() {
        String[] columns = {"Time", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        tableModel = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        weekTable = new JTable(tableModel);
        weekTable.setRowHeight(60); // 增加行高以容納事件

        weekTable.setDefaultRenderer(Object.class, new WeekCellRenderer());
        weekTable.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("unchecked")
            public void mouseClicked(MouseEvent e) {
                int row = weekTable.rowAtPoint(e.getPoint());
                int col = weekTable.columnAtPoint(e.getPoint());
                if (col > 0) {
                    Object cellValue = weekTable.getValueAt(row, col);
                    if (cellValue instanceof JPanel panel) {
                        Object eventsObj = panel.getClientProperty("events");
                        if (eventsObj instanceof List<?> events && !events.isEmpty()) {
                            Object first = events.get(0);
                            if (first instanceof EventInfo) {
                                controller.showEventDialog((EventInfo) first);
                                return;
                            }
                        }
                    }

                    LocalDate selectedDate = startOfWeek.plusDays(col - 1);
                    controller.showNewEventDialog(selectedDate);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(weekTable);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    // 新增：提供給 Controller 調用的更新方法
    public void update(LocalDate date) {
        this.startOfWeek = getStartOfCurrentWeek(date);
        updateWeek();
    }

    private void updateWeek() {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        String weekRange = startOfWeek.format(formatter) + " - " + endOfWeek.format(formatter) + ", " + startOfWeek.getYear();
        weekLabel.setText(weekRange);

        // 獲取本週的所有事件
        List<EventInfo> allEvents = jsonService.getAllEvents();
        List<EventInfo> weekEvents = getEventsForWeek(allEvents, startOfWeek);

        tableModel.setRowCount(0);
        for (int hour = 0; hour < 24; hour++) {
            Object[] row = new Object[8];
            row[0] = String.format("%02d:00", hour);

            // 為每一天檢查是否有事件
            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                LocalDate currentDate = startOfWeek.plusDays(dayIndex);
                List<EventInfo> dayHourEvents = getEventsForDateAndHour(weekEvents, currentDate, hour);

                if (!dayHourEvents.isEmpty()) {
                    row[dayIndex + 1] = createEventCellContent(dayHourEvents, currentDate);
                } else {
                    row[dayIndex + 1] = "";
                }
            }
            tableModel.addRow(row);
        }
    }

    // 新增：獲取指定週的所有事件
    private List<EventInfo> getEventsForWeek(List<EventInfo> allEvents, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return allEvents.stream()
                .filter(event -> {
                    LocalDate eventDate = Instant.ofEpochMilli(event.start.dateTime.value)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return !eventDate.isBefore(weekStart) && !eventDate.isAfter(weekEnd);
                })
                .toList();
    }

    // 新增：獲取指定日期和小時的事件
    private List<EventInfo> getEventsForDateAndHour(List<EventInfo> events, LocalDate date, int hour) {
        return events.stream()
                .filter(event -> {
                    LocalDateTime eventDateTime = Instant.ofEpochMilli(event.start.dateTime.value)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    LocalDate eventDate = eventDateTime.toLocalDate();
                    int eventHour = eventDateTime.getHour();

                    // 檢查事件是否在指定日期和小時範圍內
                    if (!eventDate.equals(date)) {
                        return false;
                    }

                    // 如果事件有結束時間，檢查是否跨越此小時
                    if (event.end != null && event.end.dateTime != null) {
                        LocalDateTime endDateTime = Instant.ofEpochMilli(event.end.dateTime.value)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        int endHour = endDateTime.getHour();

                        // 事件在此小時範圍內（開始時間 <= hour < 結束時間）
                        return eventHour <= hour && hour < endHour;
                    } else {
                        // 如果沒有結束時間，只檢查開始時間
                        return eventHour == hour;
                    }
                })
                .toList();
    }

    // 新增：創建事件儲存格內容
    private JPanel createEventCellContent(List<EventInfo> events, LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.putClientProperty("events", events);

        // 最多顯示2個事件，避免過度擁擠
        int maxEventsToShow = Math.min(events.size(), 2);
        for (int i = 0; i < maxEventsToShow; i++) {
            EventInfo event = events.get(i);

            // 格式化事件顯示
            LocalDateTime startTime = Instant.ofEpochMilli(event.start.dateTime.value)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            String timeStr = startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            String displayText = timeStr + " " + event.summary;

            JPanel eventPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(displayText);
            label.setFont(new Font("SansSerif", Font.PLAIN, 10));
            label.setForeground(Color.WHITE);
            eventPanel.add(label, BorderLayout.CENTER);
            eventPanel.setOpaque(true);
            eventPanel.setBackground(new Color(70, 130, 180));
            eventPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(50, 100, 150), 1),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));


            panel.add(eventPanel);

            if (i < maxEventsToShow - 1) {
                panel.add(Box.createVerticalStrut(2));
            }
        }

        // 如果有更多事件，顯示數量提示
        if (events.size() > maxEventsToShow) {
            JLabel moreLabel = new JLabel("+" + (events.size() - maxEventsToShow) + " more");
            moreLabel.setFont(new Font("SansSerif", Font.ITALIC, 9));
            moreLabel.setForeground(Color.GRAY);
            panel.add(moreLabel);
        }

        return panel;
    }

    private void changeWeek(int delta) {
        startOfWeek = startOfWeek.plusWeeks(delta);
        updateWeek();
    }

    private LocalDate getStartOfCurrentWeek(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return date.minusDays(dayOfWeek.getValue() % 7);
    }

    private class WeekCellRenderer extends JLabel implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            // 處理時間列（第一列）
            if (column == 0) {
                this.setText(value != null ? value.toString() : "");
                this.setOpaque(true);
                this.setBackground(new Color(240, 240, 240)); // 淺灰色背景
                this.setHorizontalAlignment(SwingConstants.CENTER);
                this.setFont(new Font("SansSerif", Font.BOLD, 11));
                return this;
            }

            // 處理事件內容
            if (value instanceof JPanel) {
                JPanel panel = (JPanel) value;
                if (isSelected) {
                    panel.setBackground(new Color(200, 220, 255));
                    // 遞迴更新子組件背景色
                    updatePanelBackground(panel, new Color(200, 220, 255));
                }
                return panel;
            }

            // 處理空儲存格
            this.setText(value != null ? value.toString() : "");
            this.setOpaque(true);
            this.setBackground(isSelected ? new Color(200, 220, 255) : Color.WHITE);
            this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setVerticalAlignment(SwingConstants.TOP);

            // 添加網格線效果
            this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));

            return this;
        }

        // 輔助方法：更新面板背景色
        private void updatePanelBackground(Container container, Color color) {
            for (Component comp : container.getComponents()) {
                if (comp instanceof JPanel) {
                    comp.setBackground(color);
                    updatePanelBackground((Container) comp, color);
                }
            }
        }
    }
}