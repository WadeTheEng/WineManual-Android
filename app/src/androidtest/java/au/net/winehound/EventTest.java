package au.net.winehound;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import au.net.winehound.domain.Event;

public class EventTest extends WineHoundTestCase {


    public void testDownloadEvents(){

    }

    public void testGetMonthEvents() throws Exception{
        createEvents();

        // Get the events
        List<Event> events = service.getEvents(2012, Calendar.JUNE);

        // Check that the right ones come out
        assertEquals(2, events.size());

        assertEquals("During event", events.get(0).getName());
        assertEquals("My birthday!", events.get(1).getName());

        // Go long before any of our events
        assertEquals(0, service.getEvents(2012, Calendar.JANUARY).size());

        // Go long after any of our events
        assertEquals(0, service.getEvents(2012, Calendar.DECEMBER).size());
    }

    public void testEventsByDay() throws Exception{
        createEvents();

        Map<Integer, List<Event>> eventsByDay = Event.organiseByDay(2012, Calendar.JUNE, service.getEvents(2012, Calendar.JUNE));

        // Check we have 30 days in june
        assertEquals(30, eventsByDay.keySet().size());

        // Make sure that the during event occurs on all days
        assertEquals(1, eventsByDay.get(1).size());
        assertEquals("During event", eventsByDay.get(1).get(0).getName());
        assertEquals("During event", eventsByDay.get(30).get(0).getName());

        // Make sure my birthday only occurs on 1 day
        assertEquals(2, eventsByDay.get(19).size());
        assertEquals("During event", eventsByDay.get(19).get(0).getName());
        assertEquals("My birthday!", eventsByDay.get(19).get(1).getName());

    }

    private void createEvents() throws SQLException {
        // Insert some events.  We will use 6/2012 as our target month

        // An event that starts and ends before our month
        service.createOrUpdateEvent(new Event(1, "Before event", getDate(1,Calendar.MAY, 2012), getDate(2, Calendar.MAY, 2012)));

        // An event that covers our month
        service.createOrUpdateEvent(new Event(2, "During event", getDate(20,Calendar.MAY, 2012), getDate(5, Calendar.JULY, 2012)));

        // An event that starts and ends in the month
        service.createOrUpdateEvent(new Event(3, "My birthday!", getDate(19, Calendar.JUNE, 2012), getDate(19, Calendar.JUNE, 2012)));

        // TODO A sub event

        // An event that starts and ends after the month
        service.createOrUpdateEvent(new Event(4, "After event", getDate(7,Calendar.JULY, 2012), getDate(7, Calendar.JULY, 2012)));
    }

    private Date getDate(int day, int month, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

}
