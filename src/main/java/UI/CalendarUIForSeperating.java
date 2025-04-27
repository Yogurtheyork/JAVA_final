package UI;

import UI.controller.CalendarController;
import UI.service.EventService;
import UI.CalendarComponents.Calender.CalendarView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class CalendarUIForSeperating extends CalendarUIHeader {

    private JButton btnWeek, btnMonth, btnYear;
    private JButton btn_lastMonth, btn_nextMonth;
    private JButton toNow;

    private JLabel[] labels;

    private int currentYear, currentMonth, currentDay;

    // Main panel for different views
    private JPanel calendarPanel;

    // Store reference to month title label
    private JLabel monthTitleLabel;

    private enum ViewMode {WEEK, MONTH, YEAR}
    private ViewMode currentView = ViewMode.MONTH; // Default view

    // Event management
    private final List<CalendarEvent> events = new ArrayList<>();
    private static final String EVENT_FILE = "src/main/resources/calendar_events.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private CalendarView calendarView;
    private CalendarController controller;

    //constructor
    public CalendarUIForSeperating() {
        initComponents();
        setupUI();
    }

    private void initComponents() {
        EventService eventService = new EventService();
        controller = new CalendarController(eventService, null); // Initialize controller first
        calendarView = new CalendarView(eventService, controller); // Then create view with controller
        controller.setView(calendarView); // Set the view in controller
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Initialize the clock label
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        TaiwanTime = new JLabel(df.format(new Date()), JLabel.CENTER);
        TaiwanTime.setFont(new Font("微軟正黑體", Font.PLAIN, 20));

        // Initialize view buttons
        btnWeek = new JButton("週");
        btnWeek.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        btnWeek.addActionListener(controller);

        btnMonth = new JButton("月");
        btnMonth.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        btnMonth.addActionListener(controller);

        btnYear = new JButton("年");
        btnYear.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        btnYear.addActionListener(controller);

        toNow = new JButton("今天");
        toNow.setOpaque(false);
        toNow.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        toNow.addActionListener(controller);

        // Search panel
        JPanel jp_search = new JPanel();
        jp_search.setOpaque(false);
        jp_search.setBorder(new TitledBorder("查詢日期"));
        jp_search.add(btnYear);
        jp_search.add(btnMonth);
        jp_search.add(btnWeek);
        jp_search.add(new JLabel("         "));
        jp_search.add(toNow);

        // Top panel layout
        JPanel jp_top = new JPanel(new BorderLayout());
        jp_top.setOpaque(false);
        jp_top.add(TaiwanTime, BorderLayout.NORTH);
        jp_top.add(jp_search, BorderLayout.CENTER);

        // Navigation buttons
        btn_lastMonth = new JButton("<<");
        btn_lastMonth.setBorder(null);
        btn_lastMonth.setOpaque(false);
        btn_lastMonth.setBackground(Color.darkGray);
        btn_lastMonth.setForeground(Color.WHITE);
        btn_lastMonth.setFont(new Font("微軟正黑體", Font.PLAIN, 30));
        btn_lastMonth.addActionListener(controller);

        btn_nextMonth = new JButton(">>");
        btn_nextMonth.setBorder(null);
        btn_nextMonth.setOpaque(false);
        btn_nextMonth.setBackground(Color.darkGray);
        btn_nextMonth.setForeground(Color.WHITE);
        btn_nextMonth.setFont(new Font("微軟正黑體", Font.PLAIN, 30));
        btn_nextMonth.addActionListener(controller);

        // Add components to main frame
        this.setBackground(new Color(202, 199, 198));
        this.add(jp_top, BorderLayout.NORTH);
        this.add(calendarView.getCalendarPanel(), BorderLayout.CENTER);
        this.add(btn_lastMonth, BorderLayout.WEST);
        this.add(btn_nextMonth, BorderLayout.EAST);

        // Set window properties
        setVisible(true);
        setSize(500, 500);

        // Start time update thread
        startTimeThread();
    }

    private void startTimeThread() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                TaiwanTime.setText(df.format(new Date()));
            }
        }, 0, 1000);
    }

    // Event class to store calendar events
    public static class CalendarEvent {
        private String title;
        private LocalDate date;
        private String start;
        private String end;
        private String description;
        private String googleCalendarId; // For future Google Calendar API integration

        public CalendarEvent(String title, LocalDate date, String start, String end, String description) {
            this.title = title;
            this.date = date;
            this.start = start;
            this.end = end;
            this.description = description;
            this.googleCalendarId = "";
        }

        public JSONObject toJSON() {
            JSONObject eventJson = new JSONObject();
            eventJson.put("title", title);
            eventJson.put("date", date.format(DATE_FORMAT));
            eventJson.put("start", start);
            eventJson.put("end", end);
            eventJson.put("description", description);
            eventJson.put("googleCalendarId", googleCalendarId);
            return eventJson;
        }

        public static CalendarEvent fromJSON(JSONObject jsonObject) {
            String title = (String) jsonObject.get("title");
            LocalDate date = LocalDate.parse((String) jsonObject.get("date"), DATE_FORMAT);
            String start = (String) jsonObject.get("start");
            String end = (String) jsonObject.get("end");
            String description = (String) jsonObject.get("description");

            CalendarEvent event = new CalendarEvent(title, date, start, end, description);
            String googleId = (String) jsonObject.get("googleCalendarId");
            if (googleId != null) {
                event.googleCalendarId = googleId;
            }
            return event;
        }

        // Getters
        public String getTitle() { return title; }
        public LocalDate getDate() { return date; }
        public String getStart() { return start; }
        public String getEnd() { return end; }
        public String getDescription() { return description; }
        public String getGoogleCalendarId() { return googleCalendarId; }

        // Setters
        public void setGoogleCalendarId(String id) { this.googleCalendarId = id; }
    }

    private void initializeMonthView() {
        calendarPanel.removeAll();

        // Create month title label
        monthTitleLabel = new JLabel("", JLabel.CENTER);
        monthTitleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        monthTitleLabel.setText(currentYear + "年" + currentMonth + "月");

        JPanel jp_display = new JPanel(new GridLayout(7, 7));
        jp_display.setOpaque(false);
        labels = new JLabel[49];
        for (int i = 0; i < 49; i++) {
            labels[i] = new JLabel(" ", JLabel.CENTER);
            labels[i].setFont(new Font("微軟正黑體", Font.PLAIN, 35));

            // Add click listener to calendar dates for event editing
            if (i >= 7) {
                final int index = i;
                labels[i].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!labels[index].getText().isEmpty()) {
                            int day = Integer.parseInt(labels[index].getText());
                            //showEventDialog(day);
                        }
                    }
                });
            }

            jp_display.add(labels[i]);
        }

        // Set weekday headers
        labels[0].setText("日");
        labels[1].setText("一");
        labels[2].setText("二");
        labels[3].setText("三");
        labels[4].setText("四");
        labels[5].setText("五");
        labels[6].setText("六");
        for (int i = 0; i < 7; i++) {
            labels[i].setForeground(Color.WHITE);
            labels[i].setBackground(Color.darkGray);
            labels[i].setOpaque(true);
        }

        calendarPanel.add(monthTitleLabel, BorderLayout.NORTH);
        calendarPanel.add(jp_display, BorderLayout.CENTER);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void showEventDialog(int day) {
        // Create dialog for adding/editing events
        LocalDate selectedDate = LocalDate.of(currentYear, currentMonth, day);

        // Get existing events for this day
        List<CalendarEvent> dayEvents = getEventsForDate(selectedDate);

        JDialog dialog = new JDialog(this, currentYear + "年" + currentMonth + "月" + day + "日 事件管理", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // Event list
        DefaultListModel<String> eventListModel = new DefaultListModel<>();
        if (dayEvents.isEmpty()) {
            eventListModel.addElement("尚無事件");
        } else {
            for (CalendarEvent event : dayEvents) {
                eventListModel.addElement(event.getEnd() + " - " + event.getTitle());
            }
        }

        JList<String> eventList = new JList<>(eventListModel);
        JScrollPane eventScrollPane = new JScrollPane(eventList);

        // Event details panel
        JPanel eventDetailsPanel = new JPanel(new BorderLayout());
        eventDetailsPanel.setBorder(BorderFactory.createTitledBorder("事件詳情"));

        JTextArea eventDetails = new JTextArea();
        eventDetails.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(eventDetails);
        eventDetailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("新增事件");
        JButton editButton = new JButton("編輯事件");
        JButton deleteButton = new JButton("刪除事件");
        JButton closeButton = new JButton("關閉");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // Event list selection listener
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = eventList.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < dayEvents.size()) {
                    CalendarEvent selectedEvent = dayEvents.get(selectedIndex);
                    eventDetails.setText("標題: " + selectedEvent.getTitle() + "\n" +
                            "時間: " + selectedEvent.getEnd() + "\n" +
                            "描述: " + selectedEvent.getDescription());
                }
            }
        });

        // Add button action
        addButton.addActionListener(e -> {
            addNewEvent(selectedDate);
            dialog.dispose();
        });

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedIndex = eventList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < dayEvents.size()) {
                editEvent(dayEvents.get(selectedIndex));
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "請先選擇一個事件");
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedIndex = eventList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < dayEvents.size()) {
                if (JOptionPane.showConfirmDialog(dialog,
                        "確定要刪除事件 \"" + dayEvents.get(selectedIndex).getTitle() + "\"?",
                        "確認刪除", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    events.remove(dayEvents.get(selectedIndex));
                    saveEvents();
                    showCalendar(currentYear, currentMonth);
                    dialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "請先選擇一個事件");
            }
        });

        // Close button action
        closeButton.addActionListener(e -> dialog.dispose());

        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                eventScrollPane, eventDetailsPanel);
        splitPane.setDividerLocation(150);

        dialog.add(splitPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addNewEvent(LocalDate date) {
        JDialog dialog = new JDialog(this, "新增事件", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Event input components
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("事件名稱:"));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("事件開始:"));
        JTextField timeFieldStart = new JTextField("HH:MM");
        inputPanel.add(timeFieldStart);

        inputPanel.add(new JLabel("事件結束:"));
        JTextField timeFieldEnd = new JTextField("HH:MM");
        inputPanel.add(timeFieldEnd);

        inputPanel.add(new JLabel("備註:"));
        JTextField descField = new JTextField();
        inputPanel.add(descField);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "請輸入事件名稱");
                return;
            }

            CalendarEvent newEvent = new CalendarEvent(
                    titleField.getText(),
                    date,
                    timeFieldStart.getText(),
                    timeFieldEnd.getText(),
                    descField.getText()
            );

            events.add(newEvent);
            saveEvents();
            showCalendar(currentYear, currentMonth);
            dialog.dispose();
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editEvent(CalendarEvent event) {
        JDialog dialog = new JDialog(this, "編輯事件", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Event input components
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("事件名稱:"));
        JTextField titleField = new JTextField(event.getTitle());
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("事件開始時間:"));
        JTextField timeFieldStart = new JTextField("HH:MM");
        inputPanel.add(timeFieldStart);

        inputPanel.add(new JLabel("事件結束時間:"));
        JTextField timeFieldEnd = new JTextField("HH:MM");
        inputPanel.add(timeFieldEnd);

        inputPanel.add(new JLabel("備註:"));
        JTextField descField = new JTextField(event.getDescription());
        inputPanel.add(descField);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "請輸入事件名稱");
                return;
            }

            // Update the event
            events.remove(event);

            CalendarEvent updatedEvent = new CalendarEvent(
                    titleField.getText(),
                    event.getDate(),
                    timeFieldStart.getText(),
                    timeFieldEnd.getText(),
                    descField.getText()
            );

            // Preserve Google Calendar ID if exists
            if (!event.getGoogleCalendarId().isEmpty()) {
                updatedEvent.setGoogleCalendarId(event.getGoogleCalendarId());
            }

            events.add(updatedEvent);
            saveEvents();
            showCalendar(currentYear, currentMonth);
            dialog.dispose();
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void setToday() {
        int year, month, day;
        year = LocalDate.now().getYear();
        month = LocalDate.now().getMonthValue();
        day = LocalDate.now().getDayOfMonth();
        currentYear = year;
        currentMonth = month;
        currentDay = day;
        showCalendar(year, month);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int year, month;

        if (e.getSource() == toNow) {
            setToday();
            return;
        }

        if (e.getSource() == btnWeek) {
            currentView = ViewMode.WEEK;
            showWeekView();
            return;
        }

        if (e.getSource() == btnMonth) {
            currentView = ViewMode.MONTH;
            initializeMonthView();
            showCalendar(currentYear, currentMonth);
            return;
        }

        if (e.getSource() == btnYear) {
            currentView = ViewMode.YEAR;
            showYearView();
            return;
        }

        if (e.getSource() == btn_lastMonth) {
            switch (currentView) {
                case WEEK:
                    // Navigate to previous week
                    LocalDate prevWeek = LocalDate.of(currentYear, currentMonth, currentDay).minusWeeks(1);
                    currentYear = prevWeek.getYear();
                    currentMonth = prevWeek.getMonthValue();
                    currentDay = prevWeek.getDayOfMonth();
                    showWeekView();
                    break;

                case MONTH:
                    year = currentYear;
                    month = currentMonth - 1;
                    if (month < 1) {
                        month = 12;
                        --year;
                    }
                    showCalendar(year, month);
                    break;

                case YEAR:
                    showYearView(currentYear - 1);
                    break;
            }
            return;
        }

        if (e.getSource() == btn_nextMonth) {
            switch (currentView) {
                case WEEK:
                    // Navigate to next week
                    LocalDate nextWeek = LocalDate.of(currentYear, currentMonth, currentDay).plusWeeks(1);
                    currentYear = nextWeek.getYear();
                    currentMonth = nextWeek.getMonthValue();
                    currentDay = nextWeek.getDayOfMonth();
                    showWeekView();
                    break;

                case MONTH:
                    year = currentYear;
                    month = currentMonth + 1;
                    if (month > 12) {
                        month = 1;
                        ++year;
                    }
                    showCalendar(year, month);
                    break;

                case YEAR:
                    showYearView(currentYear + 1);
                    break;
            }
        }
    }

    private int showCalendar(int year, int month) {
        currentYear = year;
        currentMonth = month;

        // Update month title if in month view
        if (currentView == ViewMode.MONTH && monthTitleLabel != null) {
            monthTitleLabel.setText(year + "年" + month + "月");
        }

        // Clear previous dates
        for (int i = 7; i < 49; ++i) {
            labels[i].setText("");
            labels[i].setForeground(Color.black);
            labels[i].setBackground(null);
            labels[i].setOpaque(false);
            labels[i].setToolTipText(null);
        }

        // Set up calendar
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar class months are 0-based
        int daysOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstWeekOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Fill in days
        for (int i = 0; i < daysOfMonth; ++i) {
            int dayIndex = 7 + firstWeekOfMonth + i;
            labels[dayIndex].setText(i + 1 + "");

            // Highlight today
            Calendar today = Calendar.getInstance();
            if (year == today.get(Calendar.YEAR) &&
                    month - 1 == today.get(Calendar.MONTH) &&
                    i + 1 == today.get(Calendar.DAY_OF_MONTH)) {

                labels[dayIndex].setBackground(new Color(173, 216, 230)); // Light blue
                labels[dayIndex].setOpaque(true);
            }

            // Check for events on this date
            LocalDate date = LocalDate.of(year, month, i + 1);
            List<CalendarEvent> dayEvents = getEventsForDate(date);

            if (!dayEvents.isEmpty()) {
                // Mark days with events
                if (labels[dayIndex].getBackground() == null || !labels[dayIndex].isOpaque()) {
                    labels[dayIndex].setBackground(new Color(230, 230, 250)); // Light lavender
                    labels[dayIndex].setOpaque(true);
                }

                // Create tooltip for events
                StringBuilder tooltip = new StringBuilder("<html>");
                for (CalendarEvent event : dayEvents) {
                    tooltip.append(event.getEnd()).append(" - ").append(event.getTitle()).append("<br>");
                }
                tooltip.append("</html>");
                labels[dayIndex].setToolTipText(tooltip.toString());
            }
        }

        return firstWeekOfMonth;
    }

    // Week view implementation
    private void showWeekView() {
        calendarPanel.removeAll();

        // Get the start of the week containing the current day
        LocalDate currentDate = LocalDate.of(currentYear, currentMonth, currentDay);
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);

        // Create title for week view
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        String weekTitle = startOfWeek.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) +
                " - " +
                endOfWeek.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));

        JLabel weekLabel = new JLabel(weekTitle, SwingConstants.CENTER);
        weekLabel.setFont(new Font("微軟正黑體", Font.BOLD, 20));

        // Create week table
        String[] columnNames = {"時間", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

        // Create time slots (hour-based)
        String[] timeSlots = new String[24];
        for (int i = 0; i < 24; i++) {
            timeSlots[i] = String.format("%02d:00", i);
        }

        // Create data model
        Object[][] data = new Object[timeSlots.length][columnNames.length];
        for (int i = 0; i < timeSlots.length; i++) {
            data[i][0] = timeSlots[i];
            for (int j = 1; j < columnNames.length; j++) {
                data[i][j] = ""; // Empty cells initially
            }
        }

        // Fill in events for the week
        for (int day = 0; day < 7; day++) {
            LocalDate date = startOfWeek.plusDays(day);
            List<CalendarEvent> dayEvents = getEventsForDate(date);

            for (CalendarEvent event : dayEvents) {
                // Parse event time to get hour
                String time = event.getEnd();
                try {
                    int hour = Integer.parseInt(time.split(":")[0]);
                    if (hour >= 0 && hour < 24) {
                        // Column index is day+1 because column 0 is for time labels
                        int dayColumn = day + 1;
                        // Store existing content if any
                        String existingContent = (String) data[hour][dayColumn];

                        // Append new event (or set if empty)
                        if (existingContent.isEmpty()) {
                            data[hour][dayColumn] = event.getTitle();
                        } else {
                            data[hour][dayColumn] = existingContent + "\n" + event.getTitle();
                        }
                    }
                } catch (Exception e) {
                    // Skip invalid time format
                }
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0; // Time column is not editable
            }
        };

        JTable weekTable = new JTable(model);
        weekTable.setRowHeight(30);
        weekTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        weekTable.setDefaultRenderer(Object.class, new ScheduleCellRenderer());

        // Add listener for adding events
        weekTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = weekTable.rowAtPoint(e.getPoint());
                int col = weekTable.columnAtPoint(e.getPoint());

                if (col > 0) { // Not time column
                    LocalDate date = startOfWeek.plusDays(col - 1);
                    String time = timeSlots[row];
                    showWeekEventDialog(date, time);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(weekTable);

        calendarPanel.add(weekLabel, BorderLayout.NORTH);
        calendarPanel.add(scrollPane, BorderLayout.CENTER);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void showWeekEventDialog(LocalDate date, String time) {
        JDialog dialog = new JDialog(this, date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + " " + time + " 事件", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Event input components
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("事件名稱:"));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("開始時間:"));
        JTextField timeFieldStart = new JTextField(time);
        inputPanel.add(timeFieldStart);

        inputPanel.add(new JLabel("結束時間:"));
        JTextField timeFieldEnd = new JTextField(time);
        inputPanel.add(timeFieldEnd);

        inputPanel.add(new JLabel("備註:"));
        JTextField descField = new JTextField();
        inputPanel.add(descField);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "請輸入事件名稱");
                return;
            }

            CalendarEvent newEvent = new CalendarEvent(
                    titleField.getText(),
                    date,
                    timeFieldStart.getText(),
                    timeFieldEnd.getText(),
                    descField.getText()
            );

            events.add(newEvent);
            saveEvents();
            showWeekView(); // Refresh week view
            dialog.dispose();
        });

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Year view implementation
    private void showYearView() {
        showYearView(currentYear);
    }

    private void showYearView(int year) {
        currentYear = year;
        calendarPanel.removeAll();

        // Create title for year view
        JLabel yearLabel = new JLabel(year + "年", SwingConstants.CENTER);
        yearLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));

        // Create panel for all months
        JPanel monthsPanel = new JPanel(new GridLayout(3, 4, 10, 10));

        // Create mini calendars for each month
        for (int month = 1; month <= 12; month++) {
            JPanel monthPanel = createMiniMonth(year, month);
            monthsPanel.add(monthPanel);
        }

        JScrollPane scrollPane = new JScrollPane(monthsPanel);

        calendarPanel.add(yearLabel, BorderLayout.NORTH);
        calendarPanel.add(scrollPane, BorderLayout.CENTER);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private JPanel createMiniMonth(int year, int month) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        // Month title
        String[] monthNames = {"一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"};
        JLabel titleLabel = new JLabel(monthNames[month-1], SwingConstants.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));

        // Day grid
        JPanel daysPanel = new JPanel(new GridLayout(7, 7, 1, 1));

        // Day headers
        String[] dayHeaders = {"日", "一", "二", "三", "四", "五", "六"};
        for (String dayHeader : dayHeaders) {
            JLabel label = new JLabel(dayHeader, SwingConstants.CENTER);
            label.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
            daysPanel.add(label);
        }

        // Calculate days and positions
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty cells for days before the 1st
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 10));

            // Check for events on this date
            LocalDate date = LocalDate.of(year, month, day);
            List<CalendarEvent> dayEvents = getEventsForDate(date);

            if (!dayEvents.isEmpty()) {
                dayLabel.setForeground(Color.BLUE);

                // Create tooltip for events
                StringBuilder tooltip = new StringBuilder("<html>");
                for (CalendarEvent event : dayEvents) {
                    tooltip.append(event.getEnd()).append(" - ").append(event.getTitle()).append("<br>");
                }
                tooltip.append("</html>");
                dayLabel.setToolTipText(tooltip.toString());
            }

            // Highlight today
            Calendar today = Calendar.getInstance();
            if (year == today.get(Calendar.YEAR) &&
                    month - 1 == today.get(Calendar.MONTH) &&
                    day == today.get(Calendar.DAY_OF_MONTH)) {
                dayLabel.setOpaque(true);
                dayLabel.setBackground(new Color(173, 216, 230));
            }

            // Add click listener to navigate to month view
            final int selectedMonth = month;
            final int selectedDay = day;
            dayLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    currentMonth = selectedMonth;
                    currentDay = selectedDay;
                    currentView = ViewMode.MONTH;
                    initializeMonthView();
                    showCalendar(currentYear, currentMonth);
                }
            });

            daysPanel.add(dayLabel);
        }

        // Add empty cells for remaining grid
        int remainingCells = 7 * 7 - firstDayOfWeek - daysInMonth;
        for (int i = 0; i < remainingCells; i++) {
            daysPanel.add(new JLabel(""));
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(daysPanel, BorderLayout.CENTER);

        // Add major events indicator at the bottom (if any)
        int eventCount = countEventsInMonth(year, month);
        JLabel eventsLabel = new JLabel("事件: " + eventCount, SwingConstants.CENTER);
        eventsLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        panel.add(eventsLabel, BorderLayout.SOUTH);

        return panel;
    }

    // JSON Event Storage Methods

    private void loadEvents() {
        JSONParser parser = new JSONParser();
        events.clear();

        try {
            File file = new File(EVENT_FILE);
            if (!file.exists()) {
                System.out.println("Event file does not exist yet. Will be created when events are saved.");
                return;
            }

            FileReader reader = new FileReader(file);
            JSONArray jsonEvents = (JSONArray) parser.parse(reader);

            for (Object obj : jsonEvents) {
                JSONObject jsonEvent = (JSONObject) obj;
                CalendarEvent event = CalendarEvent.fromJSON(jsonEvent);
                events.add(event);
            }

            reader.close();
            System.out.println("Loaded " + events.size() + " events.");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "無法載入事件資料: " + e.getMessage());
        }
    }

    private void saveEvents() {
        JSONArray jsonEvents = new JSONArray();
        for (CalendarEvent event : events) {
            jsonEvents.add(event.toJSON());
        }

        try {
            FileWriter writer = new FileWriter(EVENT_FILE);
            writer.write(jsonEvents.toJSONString());
            writer.flush();
            writer.close();
            System.out.println("Saved " + events.size() + " events.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "無法儲存事件資料: " + e.getMessage());
        }
    }

    // Helper methods for event management

    private List<CalendarEvent> getEventsForDate(LocalDate date) {
        List<CalendarEvent> result = new ArrayList<>();
        for (CalendarEvent event : events) {
            if (event.getDate().equals(date)) {
                result.add(event);
            }
        }
        return result;
    }

    private int countEventsInMonth(int year, int month) {
        int count = 0;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        for (CalendarEvent event : events) {
            LocalDate eventDate = event.getDate();
            if ((eventDate.isEqual(startDate) || eventDate.isAfter(startDate)) &&
                    (eventDate.isEqual(endDate) || eventDate.isBefore(endDate))) {
                count++;
            }
        }

        return count;
    }

    // Custom renderer for schedule cells (week view)
    private static class ScheduleCellRenderer extends JTextArea implements TableCellRenderer {
        public ScheduleCellRenderer() {
            setWrapStyleWord(true);
            setLineWrap(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());

            // Style the time column differently
            if (column == 0) {
                setBackground(new Color(240, 240, 240));
                setFont(new Font("微軟正黑體", Font.BOLD, 12));
            } else {
                // Use light green background for cells with events
                if (value != null && !value.toString().isEmpty()) {
                    setBackground(new Color(230, 255, 230));
                } else {
                    setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                }
                setFont(new Font("微軟正黑體", Font.PLAIN, 12));
            }

            return this;
        }
    }

    private void syncWithGoogleCalendar() {
        // TODO: Implement Google Calendar API integration
        // 1. Initialize Google Calendar API client
        // 2. Authenticate user and get authorization
        // 3. Sync local events with Google Calendar
        // 4. Update googleCalendarId for each event
        JOptionPane.showMessageDialog(this, "Google Calendar 同步功能即將推出");
    }
}*/