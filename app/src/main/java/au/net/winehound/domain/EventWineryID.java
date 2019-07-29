package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Many-to-Many linking table that joins a event to a winery
 */
@DatabaseTable
public class EventWineryID implements Serializable{

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int wineryID;

    @DatabaseField(foreign = true)
    private Event event;

    public EventWineryID() {
    }

    public EventWineryID(int wineryID, Event event) {
        this.wineryID = wineryID;
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public int getWineryID() {
        return wineryID;
    }

    public Event getEvent() {
        return event;
    }
}
