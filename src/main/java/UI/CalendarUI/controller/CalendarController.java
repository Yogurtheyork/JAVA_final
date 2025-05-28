package UI.CalendarUI.controller;

import UI.CalendarUI.model.CalendarModel;
import UI.CalendarUI.service.EventService;
import UI.CalendarUI.view.dialogs.EventDialog;
import UI.CalendarUI.view.dialogs.NewEventDialog;
import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CalendarController {

    private final CalendarModel model;
    private final EventService service;
    private Component parentComponent; // 可以是 JFrame 也可以是 JPanel

    public CalendarController(CalendarModel model, EventService service) {
        this.model = model;
        this.service = service;
    }

    public void setParentComponent(Component parent) {
        this.parentComponent = parent;
    }

    public void handleDateSelected(LocalDate date) {
        List<Event> events = service.getEventsOnDate(date);
        model.setEventsForDate(date, events);

        if (events.isEmpty()) {
            showNewEventDialog(date);
        } else {
            showEventDialog(date, events);
        }
    }

    private void showNewEventDialog(LocalDate date) {
        NewEventDialog dialog = new NewEventDialog(SwingUtilities.getWindowAncestor(parentComponent), date, (title, desc, d) -> {
            service.addEvent(title, desc, d);
            List<Event> updatedEvents = service.getEventsOnDate(d);
            model.setEventsForDate(d, updatedEvents);
        });
        dialog.setVisible(true);
    }

    private void showEventDialog(LocalDate date, List<Event> events) {
        EventDialog dialog = new EventDialog(SwingUtilities.getWindowAncestor(parentComponent), events);
        dialog.setVisible(true);
    }
}
