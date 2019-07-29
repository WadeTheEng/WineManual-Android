package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Many-to-Many linking table that joins a winery to a region
 */
@DatabaseTable
public class WineryRegionID implements Serializable{

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int regionID;

    @DatabaseField(foreign = true)
    private Winery winery;

    public WineryRegionID() {
    }

    public WineryRegionID(int regionID, Winery winery) {
        this.regionID = regionID;
        this.winery = winery;
    }

    public int getId() {
        return id;
    }

    public int getRegionID() {
        return regionID;
    }

    public Winery getWinery() {
        return winery;
    }
}
