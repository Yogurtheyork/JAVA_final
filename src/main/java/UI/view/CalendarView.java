package UI.view;

import UI.model.CalendarEvent;
import UI.service.EventService;
import UI.controller.CalendarController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class CalendarView {
    private final JPanel calendarPanel;
    private JLabel monthTitleLabel;
    private JLabel[] labels;
    private EventService eventService;
    private CalendarController controller;

    public CalendarView(EventService eventService, CalendarController controller) {
        this.eventService = eventService;
        this.controller = controller;
        this.calendarPanel = new JPanel(new BorderLayout());
        this.calendarPanel.setOpaque(false);
    }

    public JPanel getCalendarPanel() {
        return calendarPanel;
    }

    public void initializeMonthView(int year, int month) {
        calendarPanel.removeAll();

        monthTitleLabel = new JLabel("", JLabel.CENTER);
        monthTitleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        monthTitleLabel.setText(year + "年" + month + "月");

        JPanel jp_display = new JPanel(new GridLayout(7, 7));
        jp_display.setOpaque(false);
        labels = new JLabel[49];
        
        for (int i = 0; i < 49; i++) {
            labels[i] = new JLabel(" ", JLabel.CENTER);
            labels[i].setFont(new Font("微軟正黑體", Font.PLAIN, 35));

            if (i >= 7) {
                final int index = i;
                labels[i].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!labels[index].getText().isEmpty()) {
                            int day = Integer.parseInt(labels[index].getText());
                            controller.showEventDialog(day);
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

        calendarPanel.add(monthTitleLabel, BorderLayout.NORTH);
        calendarPanel.add(jp_display, BorderLayout.CENTER);
        calendarPanel.validate();
    }

    public void updateMonthView(int year, int month, int firstDayOfWeek, int daysInMonth) {
        // Clear previous dates
        for (int i = 7; i < 49; i++) {
            labels[i].setText("");
        }

        // Fill in the dates
        int day = 1;
        for (int i = 7 + firstDayOfWeek; i < 49 && day <= daysInMonth; i++) {
            labels[i].setText(String.valueOf(day));
            day++;
        }

        // Update month title
        monthTitleLabel.setText(year + "年" + month + "月");
    }

    public void showWeekView(LocalDate startDate) {
        // Implementation for week view
        // Similar to month view but with different layout
    }

    public void showYearView(int year) {
        // Implementation for year view
        // Similar to month view but with different layout
    }

    private static class ScheduleCellRenderer extends JTextArea implements TableCellRenderer {
        public ScheduleCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
} 