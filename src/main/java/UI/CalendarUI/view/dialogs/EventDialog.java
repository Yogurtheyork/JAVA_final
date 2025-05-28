package UI.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import com.google.api.services.calendar.model.Event;

public class EventDialog extends JDialog {

    private JButton closeButton;

    public EventDialog(JFrame parent, List<Event> events) {
        super(parent, "Events on Selected Date", true);

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Event event : events) {
            String time = event.getStart().getDateTime() != null ? event.getStart().getDateTime().toStringRfc3339() : event.getStart().getDate().toStringRfc3339();
            listModel.addElement("- " + event.getSummary() + " at " + time);
        }

        JList<String> eventList = new JList<>(listModel);
        eventList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(new JScrollPane(eventList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButton = new JButton("Close");
        closeButton.addActionListener((ActionEvent e) -> dispose());
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(panel);
        this.setSize(400, 300);
        this.setLocationRelativeTo(parent);
    }
}
