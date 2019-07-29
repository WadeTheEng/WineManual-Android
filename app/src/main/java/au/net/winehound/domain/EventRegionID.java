package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Many-to-Many linking table that joins a event to a region
 */
@DatabaseTable
public class EventRegionID implements Serializable{

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int regionID;

    @DatabaseField(foreign = true)
    private Event event;

    public EventRegionID() {
    }

    public EventRegionID(int regionID, Event event) {
        this.regionID = regionID;
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public int getRegionID() {
        return regionID;
    }

    public Event getEvent() {
        return event;
    }
}
