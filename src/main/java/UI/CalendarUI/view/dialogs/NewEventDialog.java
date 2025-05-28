package UI.CalendarUI.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class NewEventDialog extends JDialog {

    private JTextField summaryField;
    private JTextField locationField;
    private JTextArea descriptionArea;
    private JList<String> timeList;
    private JButton saveButton;
    private JButton cancelButton;
    private LocalDate date;

    public interface EventSaveListener {
        void onSave(String summary, String location, String description, LocalDate date, String startTime, String endTime);
    }

    public NewEventDialog(Component parentComponent, LocalDate date, EventSaveListener listener) {
        super(SwingUtilities.getWindowAncestor(parentComponent), "Add New Event", ModalityType.APPLICATION_MODAL);
        this.date = date;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 5));

        summaryField = new JTextField();
        locationField = new JTextField();
        descriptionArea = new JTextArea(3, 20);

        formPanel.add(new JLabel("Summary:"));
        formPanel.add(summaryField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));

        panel.add(formPanel, BorderLayout.NORTH);

        // Time list for half-hour slots
        DefaultListModel<String> timeModel = new DefaultListModel<>();
        for (int hour = 0; hour < 24; hour++) {
            timeModel.addElement(String.format("%02d:00", hour));
            timeModel.addElement(String.format("%02d:30", hour));
        }

        timeList = new JList<>(timeModel);
        timeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        timeList.setVisibleRowCount(8);
        timeList.setLayoutOrientation(JList.VERTICAL_WRAP);
        timeList.setFixedCellHeight(20);
        timeList.setFixedCellWidth(60);

        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(new JLabel("Select Time Range:"), BorderLayout.NORTH);
        timePanel.add(new JScrollPane(timeList), BorderLayout.CENTER);
        panel.add(timePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener((ActionEvent e) -> {
            String summary = summaryField.getText().trim();
            String location = locationField.getText().trim();
            String desc = descriptionArea.getText().trim();
            List<String> selectedTimes = timeList.getSelectedValuesList();

            if (summary.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Summary cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedTimes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one time slot", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String startTime = selectedTimes.get(0);
            String endTime = selectedTimes.get(selectedTimes.size() - 1);

            listener.onSave(summary, location, desc, date, startTime, endTime);
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(panel);
        this.pack();
        this.setLocationRelativeTo(parentComponent);
    }
}
