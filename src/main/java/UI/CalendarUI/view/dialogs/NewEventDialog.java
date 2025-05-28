package UI.CalendarUI.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class NewEventDialog extends JDialog {

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    private LocalDate date;

    public interface EventSaveListener {
        void onSave(String title, String description, LocalDate date);
    }

    public NewEventDialog(Component parentComponent, LocalDate date, EventSaveListener listener) {
        super(SwingUtilities.getWindowAncestor(parentComponent), "Add New Event", ModalityType.APPLICATION_MODAL);
        this.date = date;

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        titleField = new JTextField();
        descriptionArea = new JTextArea(4, 20);

        formPanel.add(new JLabel("Event Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener((ActionEvent e) -> {
            String title = titleField.getText().trim();
            String desc = descriptionArea.getText().trim();
            if (!title.isEmpty()) {
                listener.onSave(title, desc, date);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Title cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
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
