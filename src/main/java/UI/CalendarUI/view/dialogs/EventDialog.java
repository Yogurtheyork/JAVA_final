package UI.CalendarUI.view.dialogs;

import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventDialog extends JDialog {

    public EventDialog(Component parentComponent, List<Event> events) {
        super(SwingUtilities.getWindowAncestor(parentComponent), "Events on Selected Date", ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTextArea eventListArea = new JTextArea(15, 40);
        eventListArea.setEditable(false);

        StringBuilder sb = new StringBuilder();
        for (Event event : events) {
            sb.append("Title: ").append(event.getSummary()).append("\n");
            sb.append("Description: ").append(event.getDescription() == null ? "" : event.getDescription()).append("\n");
            sb.append("Start: ").append(event.getStart().getDateTime() != null ? event.getStart().getDateTime().toStringRfc3339() : event.getStart().getDate()).append("\n");
            sb.append("End: ").append(event.getEnd().getDateTime() != null ? event.getEnd().getDateTime().toStringRfc3339() : event.getEnd().getDate()).append("\n\n");
        }

        eventListArea.setText(sb.toString());
        panel.add(new JScrollPane(eventListArea), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(panel);
        this.pack();
        this.setLocationRelativeTo(parentComponent);
    }
}
