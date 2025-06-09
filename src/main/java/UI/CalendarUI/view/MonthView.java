package UI.CalendarUI.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.utils.DateUtils;
import UI.CalendarUI.service.JsonService;
import UI.CalendarUI.service.Event;

public class MonthView extends JPanel {

    private JTable calendarTable;
    private DefaultTableModel tableModel;
    private JLabel monthLabel;
    private JButton prevMonthBtn, nextMonthBtn, todayBtn;

    private int currentYear;
    private int currentMonth;

    private CalendarController controller;
    private JsonService jsonService; // 新增：用於讀取事件資料

    public MonthView(CalendarController controller) {
        this.controller = controller;
        this.jsonService = new JsonService(); // 初始化 JsonService

        this.setLayout(new BorderLayout());
        initHeader();
        initCalendarTable();

        LocalDate today = LocalDate.now();
        currentYear = today.getYear();
        currentMonth = today.getMonthValue();

        updateCalendar();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        // 讓月份標題可以點擊回到年視圖
        monthLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.handleYearSelected(LocalDate.of(currentYear, currentMonth, 1));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                monthLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                monthLabel.setForeground(Color.BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                monthLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                monthLabel.setForeground(Color.BLACK);
            }
        });

        headerPanel.add(monthLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        prevMonthBtn = new JButton("<");
        nextMonthBtn = new JButton(">");
        todayBtn = new JButton("Today");

        prevMonthBtn.addActionListener(e -> changeMonth(-1));
        nextMonthBtn.addActionListener(e -> changeMonth(1));
        todayBtn.addActionListener(e -> {
            LocalDate now = LocalDate.now();
            currentYear = now.getYear();
            currentMonth = now.getMonthValue();
            updateCalendar();
        });

        btnPanel.add(prevMonthBtn);
        btnPanel.add(todayBtn);
        btnPanel.add(nextMonthBtn);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);
    }

    private void initCalendarTable() {
        String[] columns = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        tableModel = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        calendarTable = new JTable(tableModel);

        calendarTable.setRowHeight(80);
        calendarTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = calendarTable.rowAtPoint(e.getPoint());
                int col = calendarTable.columnAtPoint(e.getPoint());
                Object cell = calendarTable.getValueAt(row, col);
                if (cell instanceof JPanel) {
                    JPanel dayPanel = (JPanel) cell;
                    // 從 panel 中取得 day 資訊
                    Component[] components = dayPanel.getComponents();
                    if (components.length > 0 && components[0] instanceof JLabel) {
                        JLabel dayLabel = (JLabel) components[0];
                        String dayText = dayLabel.getText();
                        if (!dayText.isEmpty()) {
                            LocalDate selectedDate = LocalDate.of(currentYear, currentMonth, Integer.parseInt(dayText));

                            // 單擊切換到週視圖
                            if (e.getClickCount() == 1) {
                                controller.handleWeekSelected(selectedDate);
                            }
                        }
                    }
                }
            }
        });

        calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());

        JScrollPane scrollPane = new JScrollPane(calendarTable);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    // 新增：提供給 Controller 調用的更新方法
    public void update(LocalDate date) {
        this.currentYear = date.getYear();
        this.currentMonth = date.getMonthValue();
        updateCalendar();
    }

    private void updateCalendar() {
        monthLabel.setText(currentYear + " - " + String.format("%02d", currentMonth));
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        LocalDate firstOfMonth = yearMonth.atDay(1);

        int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday=0
        int totalDays = yearMonth.lengthOfMonth();

        tableModel.setRowCount(0);
        Object[][] cells = new Object[6][7];

        // 獲取所有事件
        List<Event> allEvents = jsonService.getAllEvents();

        int dayCounter = 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (row == 0 && col < startDayOfWeek) {
                    cells[row][col] = createEmptyDayPanel();
                } else if (dayCounter <= totalDays) {
                    LocalDate currentDate = LocalDate.of(currentYear, currentMonth, dayCounter);
                    cells[row][col] = createDayPanel(dayCounter, getEventsForDate(allEvents, currentDate));
                    dayCounter++;
                } else {
                    cells[row][col] = createEmptyDayPanel();
                }
            }
            tableModel.addRow(cells[row]);
        }

        // 移除這行，因為我們現在直接在 updateCalendar 中處理事件顯示
        // controller.checkEvents();
    }

    // 新增：創建空的日期面板
    private JPanel createEmptyDayPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel(""), BorderLayout.NORTH);
        return panel;
    }

    // 新增：創建包含日期和事件的面板
    private JPanel createDayPanel(int day, List<Event> events) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // 日期標籤
        JLabel dayLabel = new JLabel(String.valueOf(day));
        dayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(dayLabel, BorderLayout.NORTH);

        // 事件列表
        if (!events.isEmpty()) {
            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setBackground(Color.WHITE);

            // 最多顯示3個事件，避免過度擁擠
            int maxEventsToShow = Math.min(events.size(), 3);
            for (int i = 0; i < maxEventsToShow; i++) {
                Event event = events.get(i);
                JLabel eventLabel = new JLabel(event.summary);
                eventLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
                eventLabel.setForeground(Color.BLUE);
                eventLabel.setOpaque(true);
                eventLabel.setBackground(new Color(220, 230, 255));
                eventLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
                eventsPanel.add(eventLabel);
            }

            // 如果有更多事件，顯示 "..."
            if (events.size() > maxEventsToShow) {
                JLabel moreLabel = new JLabel("...");
                moreLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
                moreLabel.setForeground(Color.GRAY);
                eventsPanel.add(moreLabel);
            }

            panel.add(eventsPanel, BorderLayout.CENTER);
        }

        return panel;
    }

    // 新增：獲取指定日期的事件
    private List<Event> getEventsForDate(List<Event> allEvents, LocalDate date) {
        return allEvents.stream()
                .filter(event -> {
                    // 將事件的時間戳轉換為 LocalDate
                    LocalDate eventDate = Instant.ofEpochMilli(event.start.dateTime.value)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return eventDate.equals(date);
                })
                .toList();
    }

    private void changeMonth(int delta) {
        currentMonth += delta;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        } else if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        updateCalendar();
    }

    private class CalendarCellRenderer extends JLabel implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JPanel) {
                JPanel panel = (JPanel) value;
                if (isSelected) {
                    panel.setBackground(new Color(200, 220, 255));
                }
                return panel;
            }
            return this;
        }
    }
}