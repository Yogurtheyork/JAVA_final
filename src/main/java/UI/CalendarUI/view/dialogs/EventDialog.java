package UI.CalendarUI.view.dialogs;

import UI.CalendarUI.controller.CalendarController;
import UI.CalendarUI.service.EventInfo;
import UI.CalendarUI.service.GoogleCalendarService;
import UI.AIArrangeUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dialog for viewing and editing events on a specific date.
 */
public class EventDialog extends JDialog {
    private final EventInfo event;
    private final GoogleCalendarService service;
    private final CalendarController controller;
    private final LocalDate date;

    private JTextField summaryField;
    private JTextField locationField;
    private JTextField startField;
    private JTextField endField;
    private JButton deleteButton;
    private JButton closeButton;
    private JButton aiArrangeButton;

    public EventDialog(Component parentComponent,
                       EventInfo event,
                       GoogleCalendarService service,
                       CalendarController controller) {
        super(SwingUtilities.getWindowAncestor(parentComponent),
                "Event Details", ModalityType.APPLICATION_MODAL);
        this.event = event;
        this.service = service;
        this.controller = controller;
        this.date = java.time.Instant.ofEpochMilli(event.start.dateTime.value)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        initUI(parentComponent);
    }

    private void initUI(Component parent) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

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
        center.add(form, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButton = new JButton("Delete");
        closeButton = new JButton("Close");
        aiArrangeButton = new JButton("AI Arrange");

        deleteButton.addActionListener(e -> onDelete());
        closeButton.addActionListener(e -> dispose());
        aiArrangeButton.addActionListener(e -> onAIArrange());

        buttonPanel.add(aiArrangeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(parent);
        populateFields();
    }

    private void populateFields() {
        summaryField.setEditable(false);
        locationField.setEditable(false);
        startField.setEditable(false);
        endField.setEditable(false);

        summaryField.setText(event.summary);
        locationField.setText(event.location);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime startZdt = java.time.Instant.ofEpochMilli(event.start.dateTime.value)
                .atZone(ZoneId.systemDefault());
        startField.setText(fmt.format(startZdt.toLocalTime()));

        if (event.end != null && event.end.dateTime != null) {
            ZonedDateTime endZdt = java.time.Instant.ofEpochMilli(event.end.dateTime.value)
                    .atZone(ZoneId.systemDefault());
            endField.setText(fmt.format(endZdt.toLocalTime()));
        } else {
            endField.setText("");
        }
    }

    private void onAIArrange() {
        AIArrangeUI aiArrangeUI = new AIArrangeUI(summaryField.getText());
        aiArrangeUI.setLocationRelativeTo(this);
        aiArrangeUI.setVisible(true);
        dispose();
    }

    private void onDelete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this event?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.deleteEvent(event.id);
            refreshViews();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting event: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
