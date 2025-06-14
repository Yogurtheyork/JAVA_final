package UI.CalendarUI.service;

public class EventInfo {
    public String summary;
    public String location;
    public DateTimeWrapper start;
    public DateTimeWrapper end;
    public String id;

    public static class DateTimeWrapper {
        public InnerDateTime dateTime;

        public static class InnerDateTime {
            public long value; // 這是 UTC timestamp in milliseconds
            public boolean dateOnly;
            public int tzShift;
        }
    }
}
