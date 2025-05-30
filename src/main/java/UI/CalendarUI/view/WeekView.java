package UI.CalendarUI.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import UI.CalendarUI.controller.CalendarController;

public class WeekView extends JPanel {

    private JTable weekTable;
    private DefaultTableModel tableModel;
    private JLabel weekLabel;
    private JButton prevWeekBtn, nextWeekBtn, todayBtn;

    private LocalDate startOfWeek;

    private CalendarController controller;

    public WeekView(CalendarController controller) {
        this.controller = controller;

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
        weekTable.setRowHeight(40);

        weekTable.setDefaultRenderer(Object.class, new WeekCellRenderer());
        weekTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = weekTable.rowAtPoint(e.getPoint());
                int col = weekTable.columnAtPoint(e.getPoint());
                if (col > 0) {
                    LocalDate selectedDate = startOfWeek.plusDays(col - 1);
                    controller.handleDateSelected(selectedDate);
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

        tableModel.setRowCount(0);
        for (int hour = 0; hour < 24; hour++) {
            Object[] row = new Object[8];
            row[0] = String.format("%02d:00", hour);
            for (int i = 1; i < 8; i++) {
                row[i] = "";
            }
            tableModel.addRow(row);
        }
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
            this.setText(value != null ? value.toString() : "");
            this.setOpaque(true);
            this.setBackground(Color.WHITE);
            this.setHorizontalAlignment(SwingConstants.LEFT);
            return this;
        }
    }
}