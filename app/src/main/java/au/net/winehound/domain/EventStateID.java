package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class EventStateID implements Serializable{

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int stateID;

    @DatabaseField(foreign = true)
    private Event event;

    public EventStateID() {

    }

    public EventStateID(int stateID, Event event) {
        this.stateID = stateID;
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public int getStateID() {
        return stateID;
    }

    public Event getEvent() {
        return event;
    }
}
