package GoogleCalendar;


public class TestGoogleCalenderFetcher {
    
    public static void main(String[] args) {
        try {
            GoogleCalendarFetcher fetcher = new GoogleCalendarFetcher();
            fetcher.fetchAndSaveEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
