package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class WineryAmenity implements Serializable{

    // We don't use the webservices ID, as each winery has its own list of amenities
    // A bit inefficient in storage space as we duplicate all the amenitiy strings,
    // however it saves all the trouble of managing a many-to-many.
    @DatabaseField(generatedId = true)
    private int dbId;

    @DatabaseField(foreign = true)
    private Winery winery;

    @DatabaseField
    private String name;

    public WineryAmenity() {
    }

    public WineryAmenity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Winery getWinery() {
        return winery;
    }

    public void initFromWebservice(Winery winery) {
        this.winery = winery;
    }
}
