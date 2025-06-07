package UI.CalendarUI.model;

import UI.CalendarUI.service.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * A reusable panel for displaying events on a specific date.
 * The appearance changes depending on the associated view.
 */
public class CalendarPanel extends JPanel {
    public enum ViewMode { MONTH, WEEK }

    private LocalDate date;
    private List<Event> events;

    public CalendarPanel(LocalDate date, List<Event> events, ViewMode mode) {
        this.date = date;
        this.events = events;
        setOpaque(true);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        updateView(mode);
    }

    /** Update panel content when events change or view mode changes. */
    public void setEvents(List<Event> events, ViewMode mode) {
        this.events = events;
        updateView(mode);
    }

    private void updateView(ViewMode mode) {
        removeAll();
        if (mode == ViewMode.MONTH) {
            renderMonthView();
        } else {
            renderWeekView();
        }
        revalidate();
        repaint();
    }

    private void renderMonthView() {
        if (date != null) {
            JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.RIGHT);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            add(dayLabel, BorderLayout.NORTH);
        }

        if (events != null && !events.isEmpty()) {
            JPanel eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setBackground(Color.WHITE);

            int maxEventsToShow = Math.min(events.size(), 3);
            for (int i = 0; i < maxEventsToShow; i++) {
                Event e = events.get(i);
                JLabel label = new JLabel(e.summary);
                label.setFont(new Font("SansSerif", Font.PLAIN, 9));
                label.setForeground(Color.BLUE);
                eventsPanel.add(label);
            }
            if (events.size() > maxEventsToShow) {
                JLabel moreLabel = new JLabel("...");
                moreLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
                moreLabel.setForeground(Color.GRAY);
                eventsPanel.add(moreLabel);
            }
            add(eventsPanel, BorderLayout.CENTER);
        }
    }

    private void renderWeekView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        if (events != null && !events.isEmpty()) {
            int maxEventsToShow = Math.min(events.size(), 2);
            for (int i = 0; i < maxEventsToShow; i++) {
                Event e = events.get(i);
                JLabel label = new JLabel(e.summary);
                label.setFont(new Font("SansSerif", Font.PLAIN, 10));
                label.setOpaque(true);
                label.setBackground(new Color(220, 230, 255));
                label.setAlignmentX(LEFT_ALIGNMENT);
                add(label);
            }
            if (events.size() > maxEventsToShow) {
                JLabel more = new JLabel("+" + (events.size() - maxEventsToShow) + " more");
                more.setFont(new Font("SansSerif", Font.ITALIC, 9));
                more.setForeground(Color.GRAY);
                more.setAlignmentX(LEFT_ALIGNMENT);
                add(more);
            }
        }
    }
}
