package au.net.winehound.domain.webservice;


import java.util.List;

import au.net.winehound.domain.Event;
import au.net.winehound.domain.Wine;

public class EventPage extends AbstractPage {

    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }
}
