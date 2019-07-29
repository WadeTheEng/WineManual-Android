package au.net.winehound.domain;


import android.location.Location;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A lightweight version of the winery class, which is mapped to the same table and is
 * suitable for using in list views or other applications where you need to load a large
 * number of wineries.  The lightweight winery loads none of the FK relationships used in the
 * proper winery object
 */
@DatabaseTable(tableName = "Winery")
public class WineryLightweight {

    @DatabaseField(id = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private int tier;

    @DatabaseField
    private String logoUrl;


    @DatabaseField(canBeNull = true)
    private String headerPhotoThumbUrl;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public Winery.Tier getTier(){
        return Winery.Tier.fromKey(tier);
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getHeaderPhotoThumbUrl() {
        return headerPhotoThumbUrl;
    }

    public boolean hasHeaderPhoto(){
        return headerPhotoThumbUrl != null;
    }

    public String getDistanceFrom(Location lastLocation) {
        // Quick exit - null
        if(lastLocation == null){
            return "";
        }

        float [] distance = new float[1];
        Location.distanceBetween(latitude, longitude, lastLocation.getLatitude(), lastLocation.getLongitude(), distance);

        return String.format("%.1f km", distance[0] / 1000);
    }
}
