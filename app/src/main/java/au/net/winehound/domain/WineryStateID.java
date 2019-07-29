package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class WineryStateID implements Serializable{

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int stateID;

    @DatabaseField(foreign = true)
    private Winery winery;

    public WineryStateID() {

    }

    public WineryStateID(int stateID, Winery winery) {
        this.stateID = stateID;
        this.winery = winery;
    }

    public int getId() {
        return id;
    }

    public int getStateID() {
        return stateID;
    }

    public Winery getWinery() {
        return winery;
    }
}
