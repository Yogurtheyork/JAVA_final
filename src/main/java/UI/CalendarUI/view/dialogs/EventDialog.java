package UI.CalendarUI.view.dialogs;

import UI.CalendarUI.controller.CalendarController;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog for displaying and editing events on a selected date.
 */
public class EventDialog extends JDialog {
    public EventDialog(Component parentComponent, List<Event> events, CalendarController controller, java.time.LocalDate date) {
        super(SwingUtilities.getWindowAncestor(parentComponent), "Events on Selected Date", ModalityType.APPLICATION_MODAL);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        for (Event event : events) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder(event.getSummary()));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2,2,2,2);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Summary:"), gbc);
            gbc.gridx = 1;
            JTextField summaryField = new JTextField(event.getSummary(),20);
            panel.add(summaryField, gbc);

            gbc.gridx = 0; gbc.gridy++;
            panel.add(new JLabel("Location:"), gbc);
            gbc.gridx = 1;
            JTextField locationField = new JTextField(event.getLocation() == null ? "" : event.getLocation(),20);
            panel.add(locationField, gbc);

            String startStr = event.getStart().getDateTime() != null ? event.getStart().getDateTime().toStringRfc3339() : "";
            String endStr = event.getEnd().getDateTime() != null ? event.getEnd().getDateTime().toStringRfc3339() : "";

            gbc.gridx = 0; gbc.gridy++;
            panel.add(new JLabel("Start:"), gbc);
            gbc.gridx = 1;
            JTextField startField = new JTextField(startStr,20);
            panel.add(startField, gbc);

            gbc.gridx = 0; gbc.gridy++;
            panel.add(new JLabel("End:"), gbc);
            gbc.gridx = 1;
            JTextField endField = new JTextField(endStr,20);
            panel.add(endField, gbc);

            JButton updateBtn = new JButton("Update");
            JButton deleteBtn = new JButton("Delete");
            gbc.gridx = 0; gbc.gridy++;
            gbc.gridwidth = 2;
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.add(deleteBtn);
            btnPanel.add(updateBtn);
            panel.add(btnPanel, gbc);

            updateBtn.addActionListener(e -> {
                try {
                    event.setSummary(summaryField.getText().trim());
                    event.setLocation(locationField.getText().trim());
                    if (!startField.getText().trim().isEmpty()) {
                        DateTime dt = new DateTime(startField.getText().trim());
                        event.getStart().setDateTime(dt);
                    }
                    if (!endField.getText().trim().isEmpty()) {
                        DateTime dt = new DateTime(endField.getText().trim());
                        event.getEnd().setDateTime(dt);
                    }
                    controller.updateExistingEvent(event, date);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
                }
            });

            deleteBtn.addActionListener(e -> {
                controller.deleteExistingEvent(event, date);
                dispose();
            });

            mainPanel.add(panel);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(closeButton);
        mainPanel.add(bottom);

        this.setContentPane(new JScrollPane(mainPanel));
        this.pack();
        this.setLocationRelativeTo(parentComponent);
    }
}

