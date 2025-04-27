package UI.controller;

import UI.model.CalendarEvent;
import UI.service.EventService;
import UI.CalendarComponents.Calender.CalendarView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class CalendarController implements ActionListener {
    private final EventService eventService;
    private CalendarView view;
    private int currentYear;
    private int currentMonth;
    private int currentDay;

    public CalendarController(EventService eventService, CalendarView view) {
        this.eventService = eventService;
        this.view = view;
        if (view != null) {
            setToday();
        }
    }

    public void setView(CalendarView view) {
        this.view = view;
        setToday();
    }

    public void setToday() {
        LocalDate today = LocalDate.now();
        currentYear = today.getYear();
        currentMonth = today.getMonthValue();
        currentDay = today.getDayOfMonth();
        updateView();
    }

    private void updateView() {
        view.initializeMonthView(currentYear, currentMonth);
        int firstDayOfWeek = LocalDate.of(currentYear, currentMonth, 1).getDayOfWeek().getValue() % 7;
        int daysInMonth = LocalDate.of(currentYear, currentMonth, 1).lengthOfMonth();
        view.updateMonthView(currentYear, currentMonth, firstDayOfWeek, daysInMonth);
    }

    public void showEventDialog(int day) {
        LocalDate date = LocalDate.of(currentYear, currentMonth, day);
        List<CalendarEvent> events = eventService.getEventsForDate(date);
        
        // Create and show event dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Events for " + date);
        dialog.setModal(true);
        dialog.setSize(400, 300);
        
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        
        if (events.isEmpty()) {
            textArea.setText("No events for this day.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (CalendarEvent event : events) {
                sb.append("Title: ").append(event.getTitle()).append("\n");
                sb.append("Time: ").append(event.getStart()).append(" - ").append(event.getEnd()).append("\n");
                sb.append("Description: ").append(event.getDescription()).append("\n\n");
            }
            textArea.setText(sb.toString());
        }
        
        JButton addButton = new JButton("Add Event");
        addButton.addActionListener(e -> {
            dialog.dispose();
            addNewEvent(date);
        });
        
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void addNewEvent(LocalDate date) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Event");
        dialog.setModal(true);
        dialog.setSize(400, 300);
        
        JPanel panel = new JPanel(new GridLayout(5, 2));
        
        JTextField titleField = new JTextField();
        JTextField startField = new JTextField();
        JTextField endField = new JTextField();
        JTextArea descriptionArea = new JTextArea();
        
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Start Time (HH:mm):"));
        panel.add(startField);
        panel.add(new JLabel("End Time (HH:mm):"));
        panel.add(endField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            CalendarEvent event = new CalendarEvent(
                titleField.getText(),
                date,
                startField.getText(),
                endField.getText(),
                descriptionArea.getText()
            );
            eventService.addEvent(event);
            dialog.dispose();
        });
        
        panel.add(new JLabel());
        panel.add(saveButton);
        
        dialog.add(panel);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "<<" : {
                currentMonth--;
                if (currentMonth < 1) {
                    currentMonth = 12;
                    currentYear--;
                }
                updateView();
            }
            case ">>" : {
                currentMonth++;
                if (currentMonth > 12) {
                    currentMonth = 1;
                    currentYear++;
                }
                updateView();
            }
            case "今天" : setToday();
        }
    }
} 