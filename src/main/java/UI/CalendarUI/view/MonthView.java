package UI.CalendarUI.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.utils.DateUtils;

public class MonthView extends JPanel {

    private JTable calendarTable;
    private DefaultTableModel tableModel;
    private JLabel monthLabel;
    private JButton prevMonthBtn, nextMonthBtn, todayBtn;

    private int currentYear;
    private int currentMonth;

    private CalendarController controller;

    public MonthView(CalendarController controller) {
        this.controller = controller;

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
                if (cell instanceof JLabel) {
                    JLabel dayLabel = (JLabel) cell;
                    String dayText = dayLabel.getText();
                    if (!dayText.isEmpty()) {
                        LocalDate selectedDate = LocalDate.of(currentYear, currentMonth, Integer.parseInt(dayText));

                        // 雙擊進入週視圖並彈出新事件對話框，單擊選擇日期
                        if (e.getClickCount() == 1) {
                            controller.handleWeekSelectedWithNewEvent(selectedDate);
                        } else {
                            controller.handleDateSelected(selectedDate);
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

        int dayCounter = 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (row == 0 && col < startDayOfWeek) {
                    cells[row][col] = new JLabel("");
                } else if (dayCounter <= totalDays) {
                    cells[row][col] = new JLabel(String.valueOf(dayCounter));
                    dayCounter++;
                } else {
                    cells[row][col] = new JLabel("");
                }
            }
            tableModel.addRow(cells[row]);
        }
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
            if (value instanceof JLabel) {
                JLabel label = (JLabel) value;
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                label.setVerticalAlignment(SwingConstants.TOP);
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                return label;
            }
            return this;
        }
    }
}