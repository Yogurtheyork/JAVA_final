package UI.CalendarUI.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

import UI.CalendarUI.controller.CalendarController;

public class YearView extends JPanel {

    private JLabel yearLabel;
    private JButton prevYearBtn, nextYearBtn, todayBtn;
    private JPanel monthsPanel;

    private int currentYear;
    private CalendarController controller;

    public YearView(CalendarController controller) {
        this.controller = controller;

        this.setLayout(new BorderLayout());
        initHeader();
        initMonthsGrid();

        currentYear = LocalDate.now().getYear();
        updateYear();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        yearLabel = new JLabel("", SwingConstants.CENTER);
        yearLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(yearLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        prevYearBtn = new JButton("<");
        nextYearBtn = new JButton(">");
        todayBtn = new JButton("Today");

        prevYearBtn.addActionListener(e -> changeYear(-1));
        nextYearBtn.addActionListener(e -> changeYear(1));
        todayBtn.addActionListener(e -> {
            currentYear = LocalDate.now().getYear();
            updateYear();
        });

        btnPanel.add(prevYearBtn);
        btnPanel.add(todayBtn);
        btnPanel.add(nextYearBtn);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);
    }

    private void initMonthsGrid() {
        monthsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        this.add(monthsPanel, BorderLayout.CENTER);
    }

    private void updateYear() {
        yearLabel.setText(String.valueOf(currentYear));
        monthsPanel.removeAll();

        for (int i = 1; i <= 12; i++) {
            final int month = i;
            JPanel monthPanel = new JPanel(new BorderLayout());
            JButton monthBtn = new JButton(Month.of(i).getDisplayName(TextStyle.FULL, Locale.getDefault()));
            monthBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            monthBtn.setBackground(Color.WHITE);
            monthBtn.setFocusPainted(false);

            // 改為由 controller 註冊動作
            controller.registerMonthButton(month, monthBtn, () -> {
                LocalDate selectedDate = LocalDate.of(currentYear, month, 1);
                controller.handleMonthSelected(selectedDate);
            });

            JPanel miniCal = buildMiniCalendar(currentYear, month);
            monthPanel.add(monthBtn, BorderLayout.NORTH);
            monthPanel.add(miniCal, BorderLayout.CENTER);

            monthsPanel.add(monthPanel);
        }

        monthsPanel.revalidate();
        monthsPanel.repaint();
    }

    private JPanel buildMiniCalendar(int year, int month) {
        JPanel panel = new JPanel(new GridLayout(0, 7));
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (String d : days) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 10));
            panel.add(label);
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = ym.lengthOfMonth();

        for (int i = 0; i < dayOfWeek; i++) {
            panel.add(new JLabel(""));
        }

        for (int d = 1; d <= daysInMonth; d++) {
            JLabel dayLabel = new JLabel(String.valueOf(d), SwingConstants.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            panel.add(dayLabel);
        }

        return panel;
    }

    private void changeYear(int delta) {
        currentYear += delta;
        updateYear();
    }
}
