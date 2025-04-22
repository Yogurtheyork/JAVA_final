package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.TitledBorder;
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

public class CalendarUI extends JFrame implements ActionListener {
    private static JLabel TaiwanTime;
    private JButton btnWeek, btnMonth, btnYear;
    private JButton toNow;
    private JButton btn_lastMonth, btn_nextMonth;
    private JLabel[] labels;
    private int currentYear, currentMonth, currentDay;
    private JPanel calendarPanel; // Main panel for different views
    private JLabel monthTitleLabel; // Store reference to month title label
    private enum ViewMode {WEEK, MONTH, YEAR}
    private ViewMode currentView = ViewMode.MONTH; // Default view

    public CalendarUI() {
        setTitle("行事曆");
        initPanel();
        setToday();
        startTimeThread(); // Start time update thread

        // Set window properties
        setVisible(true);
        setResizable(false);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initPanel() {
        // Create main layout
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

        // Initialize calendar panel for different views
        calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setOpaque(false);

        // Create monthly view (default)
        initializeMonthView();

        // Navigation buttons
        btn_lastMonth = new JButton("<<");
        btn_lastMonth.setBorder(null);
        btn_lastMonth.setOpaque(false);
        btn_lastMonth.setBackground(Color.darkGray);
        btn_lastMonth.setForeground(Color.WHITE);
        btn_lastMonth.setFont(new Font("微軟正黑體", Font.PLAIN, 30));
        btn_lastMonth.addActionListener(this);
        btn_lastMonth.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn_lastMonth.setForeground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn_lastMonth.setForeground(Color.white);
            }
        });

        btn_nextMonth = new JButton(">>");
        btn_nextMonth.setBorder(null);
        btn_nextMonth.setOpaque(false);
        btn_nextMonth.setBackground(Color.darkGray);
        btn_nextMonth.setForeground(Color.WHITE);
        btn_nextMonth.setFont(new Font("微軟正黑體", Font.PLAIN, 30));
        btn_nextMonth.addActionListener(this);
        btn_nextMonth.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn_nextMonth.setForeground(Color.green);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn_nextMonth.setForeground(Color.white);
            }
        });

        // Add components to main frame
        this.setBackground(new Color(202, 199, 198));
        this.add(jp_top, BorderLayout.NORTH);
        this.add(calendarPanel, BorderLayout.CENTER);
        this.add(btn_lastMonth, BorderLayout.WEST);
        this.add(btn_nextMonth, BorderLayout.EAST);
        this.validate();
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
                            showEventDialog(Integer.parseInt(labels[index].getText()));
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
        JDialog dialog = new JDialog(this, currentYear + "年" + currentMonth + "月" + day + "日 事件", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Event input components
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("事件名稱:"));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("事件時間:"));
        JTextField timeField = new JTextField("HH:MM");
        inputPanel.add(timeField);

        inputPanel.add(new JLabel("備註:"));
        JTextArea descArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(descArea);
        inputPanel.add(scrollPane);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            // TODO: Implement saving event to data storage
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

    // Start a thread to update time display
    private void startTimeThread() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    TaiwanTime.setText(df.format(new Date()));
                });
            }
        }, 0, 1000); // Update every second
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
            labels[7 + firstWeekOfMonth + i].setText(i + 1 + "");

            // Highlight today
            Calendar today = Calendar.getInstance();
            if (year == today.get(Calendar.YEAR) &&
                    month - 1 == today.get(Calendar.MONTH) &&
                    i + 1 == today.get(Calendar.DAY_OF_MONTH)) {

                labels[7 + firstWeekOfMonth + i].setBackground(new Color(173, 216, 230)); // Light blue
                labels[7 + firstWeekOfMonth + i].setOpaque(true);
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
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        inputPanel.add(new JLabel("事件名稱:"));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            // TODO: Implement saving event to data storage
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
        JLabel eventsLabel = new JLabel("重大事件: 無", SwingConstants.CENTER);
        eventsLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 10));
        panel.add(eventsLabel, BorderLayout.SOUTH);

        return panel;
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
                setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                setFont(new Font("微軟正黑體", Font.PLAIN, 12));
            }

            return this;
        }
    }
}