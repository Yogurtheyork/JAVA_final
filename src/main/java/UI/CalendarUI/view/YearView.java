package UI.CalendarUI.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Month;

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
            JButton monthBtn = new JButton(Month.of(i).name());
            monthBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            monthBtn.setBackground(Color.WHITE);
            monthBtn.setFocusPainted(false);
            monthBtn.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    LocalDate selectedDate = LocalDate.of(currentYear, month, 1);
                    controller.handleDateSelected(selectedDate);
                }
            });
            monthsPanel.add(monthBtn);
        }

        monthsPanel.revalidate();
        monthsPanel.repaint();
    }

    private void changeYear(int delta) {
        currentYear += delta;
        updateYear();
    }
}
