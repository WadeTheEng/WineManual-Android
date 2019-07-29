package au.net.winehound.domain;

import android.location.Location;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.net.winehound.domain.webservice.Logo;

@DatabaseTable
public class Winery implements Serializable{

    public static final int DEFAULT_ZOOM_LEVEL = 14;

    public enum Tier{
        GoldPlus(0),
        Gold(1),
        Silver(2),
        Bronze(3),
        BasicPlus(4),
        Basic(5);

        private final int key;

        Tier(int key) {
            this.key = key;
        }

        public int getKey() {
            return this.key;
        }

        public static Tier fromKey(int key) {
            for(Tier type : Tier.values()) {
                if(type.getKey() == key) {
                    return type;
                }
            }
            return null;
        }
    }

    @DatabaseField(id = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private int type;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private double latitude;

    @SerializedName("zoom_level")
    @DatabaseField
    private Integer zoomLevel;

    @DatabaseField
    private int tier;

    @DatabaseField
    private String about;

    @SerializedName("cellar_door_description")
    @DatabaseField
    private String cellarDoorDescription;

    @SerializedName("cellar_door_photographs")
    @ForeignCollectionField(eager = true, foreignFieldName = "wineryCellarDoor", orderColumnName = "id", orderAscending = true)
    private Collection<Photograph> cellarDoorPhotographs = new ArrayList<Photograph>();

    @SerializedName("photographs")
    @ForeignCollectionField(eager = true, foreignFieldName = "wineryPhoto", orderColumnName = "id", orderAscending = true)
    private Collection<Photograph> photographs = new ArrayList<Photograph>();

    @DatabaseField(canBeNull = true)
    private String headerPhotoThumbUrl;

    @SerializedName("phone_number")
    @DatabaseField
    private String phoneNumber;

    @DatabaseField
    private String address;

    @DatabaseField
    private String timezone;

    @DatabaseField
    private String website;

    @DatabaseField
    private String facebook;

    @DatabaseField
    private String twitter;

    @DatabaseField
    private boolean visible;

    @DatabaseField
    private String email;

    @SerializedName("updated_at")
    @DatabaseField
    private String updatedAt;

    @SerializedName("any_restaurants")
    @DatabaseField
    private boolean anyRestaurants;

    @SerializedName("any_wine_clubs")
    @DatabaseField
    private boolean anyWineClubs;

    @SerializedName("any_panoramas")
    @DatabaseField
    private boolean anyPanoramas;

    // Comes from the WS - used to initialise the logo url field
    private Logo logo;

    @DatabaseField
    private String logoUrl;

    @ForeignCollectionField(eager = true)
    private Collection<WineryAmenity> amenities = new ArrayList<WineryAmenity>();


    // Contains the integer array of region id's we get from the webservice.
    // This will be converted to WineryRegionID's to save in the DB
    @SerializedName("region_ids")
    private List<Integer> webserviceRegionIDs = new ArrayList<Integer>();

    // Contains the integer array of state id's we get from the webservice.
    // This will be converted to WinerySateID's to save in the DB
    @SerializedName("state_ids")
    private List<Integer> webserviceStateIDs = new ArrayList<Integer>();

    @ForeignCollectionField(eager = true)
    private Collection<WineryRegionID> regionIds = new ArrayList<WineryRegionID>();

    @ForeignCollectionField(eager = true)
    private Collection<WineryStateID> stateIds = new ArrayList<WineryStateID>();

    @SerializedName("wine_ranges")
    @ForeignCollectionField(eager = true)
    private Collection<WineRange> wineRanges = new ArrayList<WineRange>();



    public Winery(){

    }

    /**
     * Used for unit tests
     * @param id
     * @param name
     */
    public Winery(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
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

    public String getCellarDoorDescription() {
        return cellarDoorDescription;
    }

    public Collection<Photograph> getCellarDoorPhotographs() {
        return cellarDoorPhotographs;
    }

    public Collection<Photograph> getPhotographs() {
        return photographs;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getWebsite() {
        return website;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getEmail() {
        return email;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public boolean isAnyRestaurants() {
        return anyRestaurants;
    }

    public boolean isAnyWineClubs() {
        return anyWineClubs;
    }

    public boolean isAnyPanoramas() {
        return anyPanoramas;
    }

    public Tier getTier(){
        return Tier.fromKey(tier);
    }

    public void setCellarDoorPhotographs(Collection<Photograph> cellarDoorPhotographs) {
        this.cellarDoorPhotographs = cellarDoorPhotographs;
    }

    public void setPhotographs(Collection<Photograph> photographs) {
        this.photographs = photographs;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getHeaderPhotoThumbUrl() {
        return headerPhotoThumbUrl;
    }

    public Collection<WineryAmenity> getAmenities() {
        return amenities;
    }

    public Collection<WineryRegionID> getRegionIds() {
        return regionIds;
    }

    public Collection<WineryStateID> getStateIds() {
        return stateIds;
    }

    public Collection<WineRange> getWineRanges() {
        return wineRanges;
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

    public void initFromWebservice() {

        if(logo != null){
            logoUrl = logo.getLogo().getUrl();
        }

        if (photographs == null) photographs = new ArrayList<Photograph>();

        // Set the backlink on our photographs
        for(Photograph photo: cellarDoorPhotographs){
            photo.initFromWebserviceCellarDoor(this);
        }
        for(Photograph photo: photographs){
            photo.initFromWebserviceWinery(this);
        }

        for (WineryAmenity amenity: amenities){
            amenity.initFromWebservice(this);
        }

        for (WineRange range: wineRanges){
            range.initFromWebservice(this);
        }

        if(!photographs.isEmpty()){
            headerPhotoThumbUrl = photographs.iterator().next().getThumbUrl();
        }

        // Setup our regions and states
        stateIds.clear();
        for(int stateId: webserviceStateIDs){
            stateIds.add(new WineryStateID(stateId, this));
        }

        regionIds.clear();
        for(int regionId: webserviceRegionIDs){
            regionIds.add(new WineryRegionID(regionId, this));
        }
    }
}
