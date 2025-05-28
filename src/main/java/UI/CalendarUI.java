package UI;

import UI.controller.CalendarController;
import UI.service.EventService;
import UI.view.CalendarView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import java.util.List;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class CalendarUI extends JPanel implements ActionListener {

    private static JLabel TaiwanTime;

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
    public CalendarUI() {
        initComponents();
        setupUI();
        loadEvents();
        setToday();
        checkTodayEvents();
        // 初始化完成後，加上每天通知的定時任務：
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(this::checkTodayEvents),
                0, 1, TimeUnit.DAYS);
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
        btnWeek.addActionListener(this);

        btnMonth = new JButton("月");
        btnMonth.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        btnMonth.addActionListener(this);

        btnYear = new JButton("年");
        btnYear.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        btnYear.addActionListener(this);

        toNow = new JButton("今天");
        toNow.setOpaque(false);
        toNow.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        toNow.addActionListener(this);

        // Search panel
        JPanel jp_search = new JPanel();
        jp_search.setOpaque(false);
        jp_search.setBorder(new javax.swing.border.TitledBorder("查詢日期"));
        jp_search.add(btnYear);
        jp_search.add(btnMonth);
        jp_search.add(btnWeek);
        jp_search.add(new JLabel("         "));
        jp_search.add(toNow);
        JButton btnAddEvent = new JButton("新增行程");
        btnAddEvent.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        btnAddEvent.addActionListener(e -> {
            addNewEvent(LocalDate.of(currentYear, currentMonth, currentDay));
        });

        JButton btnDeleteEvent = new JButton("刪除今日行程");
        btnDeleteEvent.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        btnDeleteEvent.addActionListener(e -> {
            LocalDate date = LocalDate.of(currentYear, currentMonth, currentDay);
            List<CalendarEvent> dayEvents = getEventsForDate(date);
            if (!dayEvents.isEmpty()) {
                if (JOptionPane.showConfirmDialog(this, "確定要刪除今天的所有事件嗎？", "確認", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    events.removeIf(ev -> ev.getStartDate() .equals(date));
                    saveEvents();
                    if (currentView == ViewMode.WEEK) {
                        showWeekView();
                    } else {
                        showCalendar(currentYear, currentMonth);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "今天沒有事件可以刪除");
            }
        });

        jp_search.add(btnAddEvent);
        jp_search.add(btnDeleteEvent);
        addDeleteAllButton(jp_search);



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
        btn_lastMonth.addActionListener(this);

        btn_nextMonth = new JButton(">>");
        btn_nextMonth.setBorder(null);
        btn_nextMonth.setOpaque(false);
        btn_nextMonth.setBackground(Color.darkGray);
        btn_nextMonth.setForeground(Color.WHITE);
        btn_nextMonth.setFont(new Font("微軟正黑體", Font.PLAIN, 30));
        btn_nextMonth.addActionListener(this);

        // 這裡新增自己的 calendarPanel！
        calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setOpaque(false);

        // Add components to main frame
        this.setBackground(new Color(202, 199, 198));
        this.add(jp_top, BorderLayout.NORTH);
        this.add(calendarPanel, BorderLayout.CENTER);
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
        private LocalDate startDate;
        private LocalDate endDate;
        private String start;
        private String end;
        private String description;
        private String googleCalendarId;

        public CalendarEvent(String title, LocalDate startDate, LocalDate endDate, String start, String end, String description) {
            this.title = title;
            this.startDate = startDate;
            this.endDate = endDate;
            this.start = start;
            this.end = end;
            this.description = description;
            this.googleCalendarId = "";
        }


        public JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            obj.put("title", title);
            obj.put("startDate", startDate.format(DATE_FORMAT));
            obj.put("endDate", endDate.format(DATE_FORMAT));
            obj.put("start", start);
            obj.put("end", end);
            obj.put("description", description);
            obj.put("googleCalendarId", googleCalendarId);
            return obj;
        }

        public static CalendarEvent fromJSON(JSONObject jsonObject) {
            String title = (String) jsonObject.get("title");

            String startDateStr = (String) jsonObject.get("startDate");
            String endDateStr = (String) jsonObject.get("endDate");
            String dateStr = (String) jsonObject.get("date"); // 舊格式欄位

            LocalDate startDate, endDate;

            if (startDateStr != null && endDateStr != null) {
                startDate = LocalDate.parse(startDateStr, DATE_FORMAT);
                endDate = LocalDate.parse(endDateStr, DATE_FORMAT);
            } else if (dateStr != null) {
                startDate = LocalDate.parse(dateStr, DATE_FORMAT);
                endDate = startDate; // 舊格式只有單日
            } else {
                throw new IllegalArgumentException("事件日期缺失");
            }

            String start = (String) jsonObject.get("start");
            String end = (String) jsonObject.get("end");
            String description = (String) jsonObject.get("description");

            CalendarEvent e = new CalendarEvent(title, startDate, endDate, start, end, description);
            String gid = (String) jsonObject.get("googleCalendarId");
            if (gid != null) e.setGoogleCalendarId(gid);
            return e;
        }


        // Getters
        public String getTitle() { return title; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getStart() { return start; }
        public String getEnd() { return end; }
        public String getDescription() { return description; }
        public String getGoogleCalendarId() { return googleCalendarId; }
        public void setGoogleCalendarId(String id) { this.googleCalendarId = id; }
    }

    private void checkTodayEvents() {
        LocalDate today = LocalDate.now();
        List<CalendarEvent> todayEvents = events.stream()
                .filter(e -> !e.getStartDate().isAfter(today) && !e.getEndDate().isBefore(today))
                .collect(Collectors.toList());

        if (!todayEvents.isEmpty()) {
            StringBuilder message = new StringBuilder("您今天有以下事件：\n");
            for (CalendarEvent event : todayEvents) {
                message.append("- ").append(event.getTitle())
                        .append("（").append(event.getStart()).append(" ~ ").append(event.getEnd()).append("）\n");
            }

            // ✅ 用 ActiveUI.mainFrame 當 parent
            JOptionPane.showMessageDialog(ActiveUI.mainFrame, message.toString(), "行程提醒", JOptionPane.INFORMATION_MESSAGE);
        }
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

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), currentYear + "年" + currentMonth + "月" + day + "日 事件管理", true);
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
                    if (currentView == ViewMode.WEEK) {
                        showWeekView();
                    } else {
                        showCalendar(currentYear, currentMonth);
                    }
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


    private void addNewEvent(LocalDate ignoredDate) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "新增事件 (可跨天)", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("事件名稱:"));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("開始日期 (YYYY-MM-DD):"));
        JTextField startDateField = new JTextField();
        inputPanel.add(startDateField);

        inputPanel.add(new JLabel("開始時間 (HH:mm):"));
        JTextField timeFieldStart = new JTextField();
        inputPanel.add(timeFieldStart);

        inputPanel.add(new JLabel("結束日期 (YYYY-MM-DD):"));
        JTextField endDateField = new JTextField();
        inputPanel.add(endDateField);



        inputPanel.add(new JLabel("結束時間 (HH:mm):"));
        JTextField timeFieldEnd = new JTextField();
        inputPanel.add(timeFieldEnd);

        inputPanel.add(new JLabel("備註:"));
        JTextField descField = new JTextField();
        inputPanel.add(descField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                LocalDate startDate = LocalDate.parse(startDateField.getText(), DATE_FORMAT);
                LocalDate endDate = LocalDate.parse(endDateField.getText(), DATE_FORMAT);

                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "請輸入事件名稱");
                    return;
                }

                CalendarEvent newEvent = new CalendarEvent(
                        title,
                        startDate,
                        endDate,
                        timeFieldStart.getText(),
                        timeFieldEnd.getText(),
                        descField.getText()
                );

                events.add(newEvent);
                saveEvents();
                if (currentView == ViewMode.WEEK) {
                    showWeekView();
                } else {
                    showCalendar(currentYear, currentMonth);
                }
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "日期格式錯誤，請使用 YYYY-MM-DD");
            }
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "編輯事件", true);
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
                    event.getStartDate(),
                    event.getEndDate(),
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
            if (currentView == ViewMode.WEEK) {
                showWeekView();
            } else {
                showCalendar(currentYear, currentMonth);
            }
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
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();
        currentYear = year;
        currentMonth = month;
        currentDay = day;

        currentView = ViewMode.MONTH; // ★★強制切回月視圖★★
        initializeMonthView(); // ★ 初始化 labels！
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

    private void addDeleteAllButton(JPanel searchPanel) {
        JButton btnDeleteAllEvents = new JButton("刪除所有行程");
        btnDeleteAllEvents.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        btnDeleteAllEvents.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "確定要刪除所有行程嗎？", "確認刪除", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                events.clear();
                saveEvents();
                if (currentView == ViewMode.WEEK) {
                    showWeekView();
                } else {
                    showCalendar(currentYear, currentMonth);
                }
                JOptionPane.showMessageDialog(this, "所有行程已刪除。", "刪除完成", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        searchPanel.add(btnDeleteAllEvents);
    }
    // Week view implementation
    private void showWeekView() {
        calendarPanel.removeAll();

        LocalDate currentDate = LocalDate.of(currentYear, currentMonth, currentDay);
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        String weekTitle = startOfWeek.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) +
                " - " +
                endOfWeek.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));

        JLabel weekLabel = new JLabel(weekTitle, SwingConstants.CENTER);
        weekLabel.setFont(new Font("微軟正黑體", Font.BOLD, 20));

        String[] columnNames = {"時間", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        String[] timeSlots = new String[24];
        for (int i = 0; i < 24; i++) {
            timeSlots[i] = String.format("%02d:00", i);
        }

        Object[][] data = new Object[timeSlots.length][columnNames.length];
        for (int i = 0; i < timeSlots.length; i++) {
            data[i][0] = timeSlots[i];
            for (int j = 1; j < columnNames.length; j++) {
                data[i][j] = "";
            }
        }

        for (int day = 0; day < 7; day++) {
            LocalDate date = startOfWeek.plusDays(day);
            List<CalendarEvent> dayEvents = getEventsForDate(date);

            for (CalendarEvent event : dayEvents) {
                try {
                    int startHour = Integer.parseInt(event.getStart().split(":" )[0]);
                    int endHour = Integer.parseInt(event.getEnd().split(":" )[0]);
                    int dayColumn = day + 1;

                    for (int hour = startHour; hour <= endHour && hour < 24; hour++) {
                        String existingContent = (String) data[hour][dayColumn];
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
                return column > 0;
            }
        };

        JTable weekTable = new JTable(model);
        weekTable.setRowHeight(30);
        weekTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        weekTable.setDefaultRenderer(Object.class, new ScheduleCellRenderer());

        weekTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = weekTable.rowAtPoint(e.getPoint());
                int col = weekTable.columnAtPoint(e.getPoint());

                if (col > 0) {
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + " " + time + " 事件", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: 事件名稱
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("事件名稱:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);

        // Row 1: 開始時間 + 結束時間 同一行
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        inputPanel.add(new JLabel("開始時間:"), gbc);

        gbc.gridx = 1;
        JTextField timeFieldStart = new JTextField(time, 8);
        inputPanel.add(timeFieldStart, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("結束時間:"), gbc);

        gbc.gridx = 3;
        JTextField timeFieldEnd = new JTextField(time, 8);
        inputPanel.add(timeFieldEnd, gbc);

        // Row 2: 備註
        gbc.gridy = 2;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("備註:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField descField = new JTextField(20);
        inputPanel.add(descField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "請輸入事件名稱");
                return;
            }

            String endTime = timeFieldEnd.getText().trim();
            if (!endTime.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "結束時間格式錯誤，請使用 HH:mm");
                return;
            }

            CalendarEvent newEvent = new CalendarEvent(
                    titleField.getText(),
                    date,
                    date,
                    timeFieldStart.getText(),
                    timeFieldEnd.getText(),
                    descField.getText()
            );

            events.add(newEvent);
            saveEvents();
            showWeekView();
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
        JLabel titleLabel = new JLabel(monthNames[month - 1], SwingConstants.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));

        JPanel daysPanel = new JPanel(new GridLayout(7, 7, 1, 1));

        String[] dayHeaders = {"日", "一", "二", "三", "四", "五", "六"};
        for (String dayHeader : dayHeaders) {
            JLabel label = new JLabel(dayHeader, SwingConstants.CENTER);
            label.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
            daysPanel.add(label);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (firstDayOfWeek == Calendar.SUNDAY) {
            firstDayOfWeek = 0;
        } else {
            firstDayOfWeek -= 1;
        }
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 10));

            LocalDate date = LocalDate.of(year, month, day);
            List<CalendarEvent> dayEvents = getEventsForDate(date);
            if (!dayEvents.isEmpty()) {
                dayLabel.setForeground(Color.BLUE);
                StringBuilder tooltip = new StringBuilder("<html>");
                for (CalendarEvent event : dayEvents) {
                    tooltip.append(event.getEnd()).append(" - ").append(event.getTitle()).append("<br>");
                }
                tooltip.append("</html>");
                dayLabel.setToolTipText(tooltip.toString());
            }

            Calendar today = Calendar.getInstance();
            if (year == today.get(Calendar.YEAR) &&
                    month - 1 == today.get(Calendar.MONTH) &&
                    day == today.get(Calendar.DAY_OF_MONTH)) {
                dayLabel.setOpaque(true);
                dayLabel.setBackground(new Color(173, 216, 230));
            }

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

        int totalCells = firstDayOfWeek + daysInMonth;
        int remainingCells = 7 * 6 - totalCells; // 6 weeks = 42 cells
        for (int i = 0; i < remainingCells; i++) {
            daysPanel.add(new JLabel(""));
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(daysPanel, BorderLayout.CENTER);

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
            if ((date.isEqual(event.getStartDate()) || date.isAfter(event.getStartDate())) &&
                    (date.isEqual(event.getEndDate()) || date.isBefore(event.getEndDate()))) {
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
            LocalDate eventDate = event.getStartDate(); // 如果你只是取事件起始那天
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
}