package UI.CalendarUI.view.dialogs;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.service.GoogleCalendarService;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog for viewing and editing events on a specific date.
 */
public class EventDialog extends JDialog {
    private final List<Event> events;
    private final GoogleCalendarService service;
    private final CalendarController controller;
    private final LocalDate date;

    private JComboBox<Event> eventSelector;
    private JTextField summaryField;
    private JTextField locationField;
    private JTextField startField;
    private JTextField endField;
    private JButton saveButton;
    private JButton deleteButton;

    public EventDialog(Component parentComponent,
                       LocalDate date,
                       List<Event> events,
                       GoogleCalendarService service,
                       CalendarController controller) {
        super(SwingUtilities.getWindowAncestor(parentComponent),
                "Edit Events", ModalityType.APPLICATION_MODAL);
        this.events = events;
        this.service = service;
        this.controller = controller;
        this.date = date;
        initUI(parentComponent);
    }

    private void initUI(Component parent) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        eventSelector = new JComboBox<>(events.toArray(new Event[0]));
        eventSelector.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null) {
                lbl.setText(value.getSummary());
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(new Color(0xEEEEEE));
            }
            return lbl;
        });
        eventSelector.addActionListener(e -> populateFields((Event) eventSelector.getSelectedItem()));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        summaryField = new JTextField();
        locationField = new JTextField();
        startField = new JTextField();
        endField = new JTextField();
        form.add(new JLabel("Summary:"));
        form.add(summaryField);
        form.add(new JLabel("Location:"));
        form.add(locationField);
        form.add(new JLabel("Start (HH:mm):"));
        form.add(startField);
        form.add(new JLabel("End (HH:mm):"));
        form.add(endField);

        JPanel center = new JPanel(new BorderLayout());
        center.add(eventSelector, BorderLayout.NORTH);
        center.add(form, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");
        JButton closeButton = new JButton("Close");

        saveButton.addActionListener(e -> onSave());
        deleteButton.addActionListener(e -> onDelete());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(parent);

        if (!events.isEmpty()) {
            populateFields(events.get(0));
        }
    }

    private void populateFields(Event event) {
        if (event == null) {
            return;
        }
        summaryField.setText(event.getSummary());
        locationField.setText(event.getLocation());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        if (event.getStart() != null && event.getStart().getDateTime() != null) {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(event.getStart().getDateTime().getValue()),
                    ZoneId.systemDefault());
            startField.setText(fmt.format(zdt.toLocalTime()));
        } else {
            startField.setText("");
        }
        if (event.getEnd() != null && event.getEnd().getDateTime() != null) {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(event.getEnd().getDateTime().getValue()),
                    ZoneId.systemDefault());
            endField.setText(fmt.format(zdt.toLocalTime()));
        } else {
            endField.setText("");
        }
    }

    private void onSave() {
        Event event = (Event) eventSelector.getSelectedItem();
        if (event == null) {
            return;
        }

        String summary = summaryField.getText().trim();
        String location = locationField.getText().trim();
        String startTimeStr = startField.getText().trim();
        String endTimeStr = endField.getText().trim();

        if (summary.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Summary and times are required", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalTime startT = LocalTime.parse(startTimeStr);
            LocalTime endT = LocalTime.parse(endTimeStr);
            if (!endT.isAfter(startT)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ZoneId zoneId = ZoneId.systemDefault();
            DateTimeFormatter rfc3339 = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            ZonedDateTime startDateTime = ZonedDateTime.of(date, startT, zoneId);
            ZonedDateTime endDateTime = ZonedDateTime.of(date, endT, zoneId);

            event.setSummary(summary);
            event.setLocation(location);

            DateTime startDt = new DateTime(rfc3339.format(startDateTime));
            DateTime endDt = new DateTime(rfc3339.format(endDateTime));
            event.setStart(new EventDateTime().setDateTime(startDt).setTimeZone(zoneId.getId()));
            event.setEnd(new EventDateTime().setDateTime(endDt).setTimeZone(zoneId.getId()));

            service.updateEvent(event);
            refreshViews();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Event event = (Event) eventSelector.getSelectedItem();
        if (event == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this event?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.deleteEvent(event.getId());
            refreshViews();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshViews() throws Exception {
        service.fetchAndSaveEvents();
        if (controller != null) {
            controller.handleMonthSelected(date);
            controller.handleWeekSelected(date);
        }
    }
}
