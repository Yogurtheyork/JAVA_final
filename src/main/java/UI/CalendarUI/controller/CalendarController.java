package UI.controller;

import UI.model.CalendarModel;
import UI.service.EventService;
import UI.view.dialogs.EventDialog;
import UI.view.dialogs.NewEventDialog;
import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class CalendarController {

    private final CalendarModel model;
    private final EventService service;
    private final JFrame parentFrame;

    public CalendarController(CalendarModel model, EventService service, JFrame parentFrame) {
        this.model = model;
        this.service = service;
        this.parentFrame = parentFrame;
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
        NewEventDialog dialog = new NewEventDialog(parentFrame, date, (title, desc, d) -> {
            service.addEvent(title, desc, d);
            List<Event> updatedEvents = service.getEventsOnDate(d);
            model.setEventsForDate(d, updatedEvents);
        });
        dialog.setVisible(true);
    }

    private void showEventDialog(LocalDate date, List<Event> events) {
        EventDialog dialog = new EventDialog(parentFrame, events);
        dialog.setVisible(true);
    }
}
