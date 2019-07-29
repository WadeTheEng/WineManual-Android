package au.net.winehound.domain;

import android.location.Location;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

@DatabaseTable
public class Region implements Serializable{

    private static final int DEFAULT_ZOOM_LEVEL = 10;

    @DatabaseField(id = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String name;

    @SerializedName("has_wineries")
    @DatabaseField
    private boolean hasWineries;

    @SerializedName("has_breweries")
    @DatabaseField
    private boolean hasBreweries;

    @SerializedName("has_cideries")
    @DatabaseField
    private boolean hasCideries;

    @SerializedName("state_id")
    @DatabaseField
    private int stateID;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private double latitude;

    @SerializedName("zoom_level")
    @DatabaseField(canBeNull = true)
    private Integer zoomLevel;

    @DatabaseField
    private String about;

    @SerializedName("phone_number")
    @DatabaseField
    private String phoneNumber;

    @SerializedName("website_url")
    @DatabaseField
    private String website;

    @DatabaseField
    private String email;

    @DatabaseField
    private String zone;

    @SerializedName("photographs")
    @ForeignCollectionField(eager = true, orderColumnName = "id", orderAscending = false)
    private Collection<Photograph> photographs = new ArrayList<Photograph>();

    @DatabaseField(canBeNull = true)
    private String headerPhotoThumbUrl;

    @SerializedName("updated_at")
    @DatabaseField
    private String updatedAt;

    @SerializedName("map_pdf")
    @DatabaseField
    private String mapPdf;

    public Region() {
    }

    public Region(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasWineries() {
        return hasWineries;
    }

    public boolean hasBreweries() {
        return hasBreweries;
    }

    public boolean hasCideries() {
        return hasCideries;
    }

    public int getStateID() {
        return stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getZoomLevel() {
        if(zoomLevel == null){
            return DEFAULT_ZOOM_LEVEL;
        }
        else{
            return zoomLevel;
        }
    }

    public String getAbout() {
        return about;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getZone() {
        return zone;
    }

    public Collection<Photograph> getPhotographs() {
        return photographs;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getMapPdf() {
        return mapPdf;
    }

    public void initFromWebservice() {
        // Set the backlink on our photographs
        if (photographs == null) photographs = new ArrayList<Photograph>();

        for(Photograph photo: photographs){
            photo.initFromWebserviceRegion(this);
        }

        if(!photographs.isEmpty()){
            headerPhotoThumbUrl = photographs.iterator().next().getThumbUrl();
        }
    }
}
