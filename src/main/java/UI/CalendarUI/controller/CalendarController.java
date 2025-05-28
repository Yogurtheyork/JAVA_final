package UI.CalendarUI.controller;

import UI.CalendarUI.model.CalendarModel;
import UI.CalendarUI.service.EventService;
import UI.CalendarUI.view.dialogs.EventDialog;
import UI.CalendarUI.view.dialogs.NewEventDialog;
import com.google.api.services.calendar.model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class CalendarController {

    private final CalendarModel model;
    private final EventService service;
    private Component parentComponent;
    private Consumer<LocalDate> switchToMonthViewHandler;
    private Consumer<LocalDate> switchToWeekViewHandler;

    public CalendarController(CalendarModel model, EventService service) {
        this.model = model;
        this.service = service;
    }

    public void setParentComponent(Component parent) {
        this.parentComponent = parent;
    }

    public void setSwitchToMonthViewHandler(Consumer<LocalDate> handler) {
        this.switchToMonthViewHandler = handler;
    }

    public void setSwitchToWeekViewHandler(Consumer<LocalDate> handler) {
        this.switchToWeekViewHandler = handler;
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

    public void handleMonthSelected(LocalDate date) {
        if (switchToMonthViewHandler != null) {
            switchToMonthViewHandler.accept(date);
        }
    }

    public void handleDaySelected(LocalDate date) {
        if (switchToWeekViewHandler != null) {
            switchToWeekViewHandler.accept(date);
        }
    }

    public void showNewEventDialog(LocalDate date) {
        NewEventDialog dialog = new NewEventDialog(
                SwingUtilities.getWindowAncestor(parentComponent),
                date,
                (summary, location, description, d, startTime, endTime) -> {
                    try {
                        // Combine date + time and convert to RFC3339 format
                        ZoneId zoneId = ZoneId.systemDefault();
                        ZonedDateTime startDateTime = ZonedDateTime.of(d, java.time.LocalTime.parse(startTime), zoneId);
                        ZonedDateTime endDateTime = ZonedDateTime.of(d, java.time.LocalTime.parse(endTime), zoneId);

                        String startRfc3339 = startDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        String endRfc3339 = endDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                        // Create and insert event
                        Event newEvent = service.createEvent(summary, location, description, startRfc3339, endRfc3339);
                        service.insertEvent(newEvent);

                        // Update model (optional: refresh UI)
                        List<Event> updatedEvents = service.getEventsOnDate(d);
                        model.setEventsForDate(d, updatedEvents);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(parentComponent, "Error saving event: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );
        dialog.setVisible(true);
    }

    private void showEventDialog(LocalDate date, List<Event> events) {
        EventDialog dialog = new EventDialog(SwingUtilities.getWindowAncestor(parentComponent), events);
        dialog.setVisible(true);
    }
}
