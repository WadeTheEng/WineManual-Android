package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class FavouriteEventID {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Event event;

    public FavouriteEventID() {
    }

    public FavouriteEventID(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
